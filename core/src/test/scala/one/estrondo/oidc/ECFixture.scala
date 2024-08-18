package one.estrondo.oidc

import Base64Ops._
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.ECGenParameterSpec
import java.security.spec.ECPublicKeySpec

object ECFixture {

  type Output = (PublicKey, PrivateKey, Jwk)

  def createRandom(algorithm: JwaAlgorithm.Ec): Output = {
    val generator = KeyPairGenerator.getInstance("EC")
    val factory   = KeyFactory.getInstance("EC")

    generator.initialize(new ECGenParameterSpec(algorithm.curveFullName))
    val pair = generator.generateKeyPair()
    val spec = factory.getKeySpec(pair.getPublic, classOf[ECPublicKeySpec])
    val x    = encodeBase64UrlEncoded(spec.getW.getAffineX)
    val y    = encodeBase64UrlEncoded(spec.getW.getAffineY)

    (
      pair.getPublic,
      pair.getPrivate,
      Jwk(
        kid = Some(Fixtures.randomId()),
        kty = Some("EC"),
        x = Some(x),
        y = Some(y),
        crv = Some(algorithm.curve),
        alg = Some(algorithm.name),
      ),
    )
  }

}
