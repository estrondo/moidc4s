package one.estrondo.oidc4s

import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.RSAPublicKeySpec
import one.estrondo.oidc4s.Base64Ops._

object RSAFixture {

  type Out = (PublicKey, PrivateKey, Jwk)

  def createRandom(algorithm: JwaAlgorithm.Rsa): Out = {
    val keyGenerator = KeyPairGenerator.getInstance("RSA")
    val pair         = keyGenerator.generateKeyPair()
    val keyFactory   = KeyFactory.getInstance("RSA")
    val publicKey    = keyFactory.getKeySpec(pair.getPublic, classOf[RSAPublicKeySpec])

    (
      pair.getPublic,
      pair.getPrivate,
      Jwk(
        alg = Some(algorithm.name),
        kid = Some(Fixtures.randomId()),
        kty = Some("RSA"),
        n = Some(encodeBase64UrlEncoded(publicKey.getModulus)),
        e = Some(encodeBase64UrlEncoded(publicKey.getPublicExponent)),
      ),
    )
  }
}
