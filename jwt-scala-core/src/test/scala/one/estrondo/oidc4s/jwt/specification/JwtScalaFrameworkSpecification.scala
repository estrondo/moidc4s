package one.estrondo.oidc4s.jwt.specification

import java.security.PrivateKey
import java.security.PublicKey
import one.estrondo.oidc4s.Context
import one.estrondo.oidc4s.ECFixture
import one.estrondo.oidc4s.Fixtures
import one.estrondo.oidc4s.HMacFixture
import one.estrondo.oidc4s.JwaAlgorithm
import one.estrondo.oidc4s.KeyDescription
import one.estrondo.oidc4s.RSAFixture
import one.estrondo.oidc4s.TestUnit
import one.estrondo.oidc4s.TestUnitContext
import one.estrondo.oidc4s.TestUnitOps
import one.estrondo.oidc4s.syntax._
import one.estrondo.oidc4s.jwt.Jwt
import one.estrondo.oidc4s.jwt.JwtScalaJsonLibrary
import org.scalatest.Assertion
import pdi.jwt.JwtAlgorithm
import pdi.jwt.JwtClaim
import pdi.jwt.algorithms.JwtAsymmetricAlgorithm
import pdi.jwt.algorithms.JwtHmacAlgorithm
import scala.reflect.ClassTag

//noinspection ConvertExpressionToSAM
class JwtScalaFrameworkSpecification[F[_]: Context, J: JwtScalaJsonLibrary: ClassTag] extends TestUnitOps {

  import _root_.pdi.jwt.{Jwt => JwtScala}

  type Generated = (String, KeyDescription)

  def generateToken(
      algorithm: JwaAlgorithm,
      jwtAlgorithm: JwtAlgorithm,
      keepAlg: Boolean = true,
  ): Generated = {
    algorithm match {
      case algorithm: JwaAlgorithm.Rsa  => generateRSA(algorithm, jwtAlgorithm, keepAlg)
      case algorithm: JwaAlgorithm.Ec   => generateEC(algorithm, jwtAlgorithm, keepAlg)
      case algorithm: JwaAlgorithm.Hmac => generateSymmetric(algorithm, jwtAlgorithm, keepAlg)
      case _                            => ???
    }
  }

  def generateRSA(algorithm: JwaAlgorithm.Rsa, jwtAlgorithm: JwtAlgorithm, keepAlg: Boolean): Generated = {
    val (pub, pri, _) = RSAFixture.createRandom(algorithm)
    generateAsymmetric(pub, pri, algorithm, jwtAlgorithm, keepAlg)
  }

  def generateEC(algorithm: JwaAlgorithm.Ec, jwtAlgorithm: JwtAlgorithm, keepAlg: Boolean): Generated = {
    val (pub, pri, _) = ECFixture.createRandom(algorithm)
    generateAsymmetric(pub, pri, algorithm, jwtAlgorithm, keepAlg)
  }

  def generateSymmetric(
      alg: JwaAlgorithm.Hmac,
      jwtAlgorithm: JwtAlgorithm,
      keepAlg: Boolean,
  ): Generated = {
    val (key, _) = HMacFixture.createRandom(alg)
    val claim    = JwtClaim(jwtId = Some(Fixtures.randomId()), issuer = Some("estrondo"))
    val token    = JwtScala.encode(claim, key, jwtAlgorithm.asInstanceOf[JwtHmacAlgorithm])

    token -> generateKeyDescription(alg, KeyDescription.Secret(key), keepAlg)
  }

  def generateAsymmetric(
      pub: PublicKey,
      pri: PrivateKey,
      algorithm: JwaAlgorithm,
      jwtAlgorithm: JwtAlgorithm,
      keepAlg: Boolean,
  ): Generated = {
    val claim = JwtClaim(jwtId = Some(Fixtures.randomId()), issuer = Some("estrondo"))
    val token = JwtScala.encode(claim, pri, jwtAlgorithm.asInstanceOf[JwtAsymmetricAlgorithm])

    token -> generateKeyDescription(algorithm, KeyDescription.Public(pub), keepAlg)
  }

  def generateKeyDescription(algorithm: JwaAlgorithm, key: KeyDescription.Key, keepAlg: Boolean): KeyDescription =
    KeyDescription(
      kid = Some(Fixtures.randomId()),
      alg = if (keepAlg) Some(algorithm) else None,
      key = key,
    )

  def generateTestUnit(
      algorithm: JwaAlgorithm,
      jwtAlgorithm: JwtAlgorithm,
      keepAlg: Boolean = true,
  ): TestUnit[F] = {
    testUnit(s"It should decode a ${algorithm.name}-token when keepAlg=$keepAlg.")(
      new TestUnitContext[F] {
        override def apply(): F[Assertion] = {
          val (token, description) = generateToken(algorithm, jwtAlgorithm, keepAlg)

          for (result <- Jwt.decode().apply(token, Some(description)).toTry) yield {
            result.success.value shouldBe an[J]
          }
        }
      },
    )
  }

  val u01 = generateTestUnit(JwaAlgorithm.Hs256, JwtAlgorithm.HS256)
  val u02 = generateTestUnit(JwaAlgorithm.Hs384, JwtAlgorithm.HS384)
  val u03 = generateTestUnit(JwaAlgorithm.Hs512, JwtAlgorithm.HS512)
  val u04 = generateTestUnit(JwaAlgorithm.Rs256, JwtAlgorithm.RS256)
  val u05 = generateTestUnit(JwaAlgorithm.Rs384, JwtAlgorithm.RS384)
  val u06 = generateTestUnit(JwaAlgorithm.Rs512, JwtAlgorithm.RS512)
  val u07 = generateTestUnit(JwaAlgorithm.Es256, JwtAlgorithm.ES256)
  val u08 = generateTestUnit(JwaAlgorithm.Es384, JwtAlgorithm.ES384)
  val u09 = generateTestUnit(JwaAlgorithm.Es512, JwtAlgorithm.ES512)

  val u10 = generateTestUnit(JwaAlgorithm.Hs256, JwtAlgorithm.HS256, keepAlg = false)
  val u11 = generateTestUnit(JwaAlgorithm.Hs384, JwtAlgorithm.HS384, keepAlg = false)
  val u12 = generateTestUnit(JwaAlgorithm.Hs512, JwtAlgorithm.HS512, keepAlg = false)

  val u20              = generateTestUnit(JwaAlgorithm.Rs256, JwtAlgorithm.RS256, keepAlg = false)
  val u21              = generateTestUnit(JwaAlgorithm.Rs384, JwtAlgorithm.RS384, keepAlg = false)
  val u22: TestUnit[F] = generateTestUnit(JwaAlgorithm.Rs512, JwtAlgorithm.RS512, keepAlg = false)

  val u30 = generateTestUnit(JwaAlgorithm.Es256, JwtAlgorithm.ES256, keepAlg = false)
  val u31 = generateTestUnit(JwaAlgorithm.Es384, JwtAlgorithm.ES384, keepAlg = false)
  val u32 = generateTestUnit(JwaAlgorithm.Es512, JwtAlgorithm.ES512, keepAlg = false)

}
