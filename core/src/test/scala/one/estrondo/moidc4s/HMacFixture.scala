package one.estrondo.moidc4s

import Base64Ops._
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec
import scala.util.Random

object HMacFixture {

  type Output = (SecretKey, Jwk)

  def createRandom(algorithm: JwaAlgorithm.Hmac): Output = {
    val key = Array.ofDim[Byte](algorithm.length)
    Random.nextBytes(key)

    (
      new SecretKeySpec(key, algorithm.fullName),
      Jwk(
        kid = Some(Fixtures.randomId()),
        kty = Some("oct"),
        alg = Some(algorithm.name),
        k = Some(encodeBase64UrlEncoded(key)),
      ),
    )
  }
}
