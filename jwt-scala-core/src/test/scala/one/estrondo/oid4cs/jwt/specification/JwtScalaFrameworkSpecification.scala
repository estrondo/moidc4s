package one.estrondo.oid4cs.jwt.specification

import java.security.PrivateKey
import java.security.PublicKey
import one.estrondo.oidc.Context
import one.estrondo.oidc.ECFixture
import one.estrondo.oidc.Fixtures
import one.estrondo.oidc.HMacFixture
import one.estrondo.oidc.JwaAlg
import one.estrondo.oidc.KeyDescription
import one.estrondo.oidc.RSAFixture
import one.estrondo.oidc.TestUnit
import one.estrondo.oidc.TestUnitContext
import one.estrondo.oidc.TestUnitOps
import one.estrondo.oidc.syntax._
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
      jwaAlg: JwaAlg,
      jwtAlgorithm: JwtAlgorithm,
      keepAlg: Boolean = true,
  ): Generated = {
    jwaAlg match {
      case JwaAlg.Rs512 => generateRSA(jwaAlg, jwtAlgorithm, keepAlg)
      case JwaAlg.Rs384 => generateRSA(jwaAlg, jwtAlgorithm, keepAlg)
      case JwaAlg.Rs256 => generateRSA(jwaAlg, jwtAlgorithm, keepAlg)
      case JwaAlg.Es256 => generateEC(jwaAlg, jwtAlgorithm, keepAlg)
      case JwaAlg.Es384 => generateEC(jwaAlg, jwtAlgorithm, keepAlg)
      case JwaAlg.Es512 => generateEC(jwaAlg, jwtAlgorithm, keepAlg)
      case JwaAlg.Hs256 => generateSymmetric(jwaAlg, 256, jwtAlgorithm, keepAlg)
      case JwaAlg.Hs384 => generateSymmetric(jwaAlg, 384, jwtAlgorithm, keepAlg)
      case JwaAlg.Hs512 => generateSymmetric(jwaAlg, 512, jwtAlgorithm, keepAlg)
    }
  }

  def generateRSA(jwaAlg: JwaAlg, jwtAlgorithm: JwtAlgorithm, keepAlg: Boolean): Generated = {
    val (pub, pri, _) = RSAFixture.createRandom(jwaAlg)
    generateAsymmetric(pub, pri, jwaAlg, jwtAlgorithm, keepAlg)
  }

  def generateEC(jwaAlg: JwaAlg, jwtAlgorithm: JwtAlgorithm, keepAlg: Boolean): Generated = {
    val (pub, pri, _) = jwaAlg match {
      case JwaAlg.Es256 => ECFixture.createRandomP256()
      case JwaAlg.Es384 => ECFixture.createRandomP384()
      case JwaAlg.Es512 => ECFixture.createRandomP512()
    }
    generateAsymmetric(pub, pri, jwaAlg, jwtAlgorithm, keepAlg)
  }

  def generateSymmetric(
      alg: JwaAlg,
      length: Int,
      jwtAlgorithm: JwtAlgorithm,
      keepAlg: Boolean,
  ): Generated = {
    val (key, _) = HMacFixture.createRandom(length, alg)
    val claim    = JwtClaim(jwtId = Some(Fixtures.randomId()), issuer = Some("estrondo"))
    val token    = JwtScala.encode(claim, key, jwtAlgorithm.asInstanceOf[JwtHmacAlgorithm])

    token -> generateKeyDescription(alg, KeyDescription.Secret(key), keepAlg)
  }

  def generateAsymmetric(
      pub: PublicKey,
      pri: PrivateKey,
      alg: JwaAlg,
      jwtAlgorithm: JwtAlgorithm,
      keepAlg: Boolean,
  ): Generated = {
    val claim = JwtClaim(jwtId = Some(Fixtures.randomId()), issuer = Some("estrondo"))
    val token = JwtScala.encode(claim, pri, jwtAlgorithm.asInstanceOf[JwtAsymmetricAlgorithm])

    token -> generateKeyDescription(alg, KeyDescription.Public(pub), keepAlg)
  }

  def generateKeyDescription(alg: JwaAlg, key: KeyDescription.Key, keepAlg: Boolean): KeyDescription = KeyDescription(
    kid = Some(Fixtures.randomId()),
    alg = if (keepAlg) Some(alg) else None,
    key = key,
  )

  def generateTestUnit(
      alg: JwaAlg,
      jwtAlgorithm: JwtAlgorithm,
      keepAlg: Boolean = true,
  ): TestUnit[F] = {
    testUnit(s"It should decode a ${alg.value}-token when keepAlg=$keepAlg.")(
      new TestUnitContext[F] {
        override def apply(): F[Assertion] = {
          val (token, description) = generateToken(alg, jwtAlgorithm, keepAlg)

          for (result <- Jwt.decode().apply(token, Some(description)).toTry) yield {
            result.success.value shouldBe an[J]
          }
        }
      },
    )
  }

  val u01 = generateTestUnit(JwaAlg.Hs256, JwtAlgorithm.HS256)
  val u02 = generateTestUnit(JwaAlg.Hs384, JwtAlgorithm.HS384)
  val u03 = generateTestUnit(JwaAlg.Hs512, JwtAlgorithm.HS512)
  val u04 = generateTestUnit(JwaAlg.Rs256, JwtAlgorithm.RS256)
  val u05 = generateTestUnit(JwaAlg.Rs384, JwtAlgorithm.RS384)
  val u06 = generateTestUnit(JwaAlg.Rs512, JwtAlgorithm.RS512)
  val u07 = generateTestUnit(JwaAlg.Es256, JwtAlgorithm.ES256)
  val u08 = generateTestUnit(JwaAlg.Es384, JwtAlgorithm.ES384)
  val u09 = generateTestUnit(JwaAlg.Es512, JwtAlgorithm.ES512)

  val u10 = generateTestUnit(JwaAlg.Hs256, JwtAlgorithm.HS256, keepAlg = false)
  val u11 = generateTestUnit(JwaAlg.Hs384, JwtAlgorithm.HS384, keepAlg = false)
  val u12 = generateTestUnit(JwaAlg.Hs512, JwtAlgorithm.HS512, keepAlg = false)

  val u20 = generateTestUnit(JwaAlg.Rs256, JwtAlgorithm.RS256, keepAlg = false)
  val u21 = generateTestUnit(JwaAlg.Rs384, JwtAlgorithm.RS384, keepAlg = false)
  val u22: TestUnit[F] = generateTestUnit(JwaAlg.Rs512, JwtAlgorithm.RS512, keepAlg = false)

  val u30 = generateTestUnit(JwaAlg.Es256, JwtAlgorithm.ES256, keepAlg = false)
  val u31 = generateTestUnit(JwaAlg.Es384, JwtAlgorithm.ES384, keepAlg = false)
  val u32 = generateTestUnit(JwaAlg.Es512, JwtAlgorithm.ES512, keepAlg = false)

}
