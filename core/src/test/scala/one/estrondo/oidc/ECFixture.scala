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

  def createRandomP512(): Output = generate("secp521r1", "P-521", JwaAlg.Es512)

  def createRandomP384(): Output = generate("secp384r1", "P-384", JwaAlg.Es384)

  def createRandomP256(): Output = generate("secp256r1", "P-256", JwaAlg.Es256)

  private def generate(curve: String, crv: String, alg: JwaAlg): Output = {
    val generator = KeyPairGenerator.getInstance("EC")
    val factory   = KeyFactory.getInstance("EC")

    generator.initialize(new ECGenParameterSpec(curve))
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
        crv = Some(crv),
        alg = Some(alg.value),
      ),
    )
  }

}
