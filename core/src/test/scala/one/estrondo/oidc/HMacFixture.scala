package one.estrondo.oidc

import java.util.UUID
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec
import scala.util.Random

object HMacFixture extends Base64Ops {

  type Output = (SecretKey, Jwk)

  def createRandomHs256(): Output = createRandom(256, JwaAlg.Hs256)

  def createRandomHs384(): Output = createRandom(384, JwaAlg.Hs384)

  def createRandomHs512(): Output = createRandom(512, JwaAlg.Hs512)

  private def createRandom(length: Int, alg: JwaAlg): Output = {
    val key = Array.ofDim[Byte](length)
    Random.nextBytes(key)

    (
      new SecretKeySpec(key, alg.alg.get.asInstanceOf[JwaAlg.Mac].value),
      Jwk(
        kid = Some(UUID.randomUUID().toString),
        kty = Some("oct"),
        alg = Some(alg.value),
        k = Some(encodeBase64UrlEncoded(key)),
      ),
    )
  }
}
