package one.estrondo.oidc.specs

import one.estrondo.oidc.Base64Ops
import one.estrondo.oidc.ECFixture
import one.estrondo.oidc.HMacFixture
import one.estrondo.oidc.Jwa
import one.estrondo.oidc.JwaAlg
import one.estrondo.oidc.KeyDescription
import one.estrondo.oidc.RSAFixture
import one.estrondo.oidc.SignatureOperations
import scala.util.Random
import scala.util.Success

class JwaSpec extends OidcSpec with Base64Ops {

  private def createRandomData(): Array[Byte] = {
    val bytes = Array.ofDim[Byte](1024 * 32)
    Random.nextBytes(bytes)
    bytes
  }

  "Jwa should" - {

    "read a valid EC Key." in {

      val (_, privateKey, jwk) = ECFixture.createRandomP521(Some(JwaAlg.Es512))
      val algorithm            = JwaAlg.read(jwk).get

      val Success(description) = Jwa(
        "EC",
        jwk,
      )

      val KeyDescription.Public(publicKey) = description.key

      val data      = createRandomData()
      val signature = SignatureOperations.sign(algorithm, data, privateKey)
      SignatureOperations.verify(algorithm, data, signature, publicKey)
    }

    "read a valid RSA Key" in {

      val (_, privateKey, jwk) = RSAFixture.createRandom(JwaAlg.Rs512)
      val algorithm            = JwaAlg.read(jwk).get

      val Success(description) = Jwa(
        "RSA",
        jwk,
      )

      val KeyDescription.Public(publicKey) = description.key

      val data      = createRandomData()
      val signature = SignatureOperations.sign(algorithm, data, privateKey)
      SignatureOperations.verify(algorithm, data, signature, publicKey)
    }

    "read a valid Hmac* key" - {
      for {
        (length, fn) <- Seq(
                          "256" -> HMacFixture.createRandomHs256 _,
                          "384" -> HMacFixture.createRandomHs384 _,
                          "512" -> HMacFixture.createRandomHs512 _,
                        )
      } {
        s"read a valid Hmac$length key" in {
          val (originalKey, jwk) = fn()
          val algorithm          = JwaAlg.read(jwk).get

          val Success(description)             = Jwa("oct", jwk)
          val KeyDescription.Secret(secretKey) = description.key

          val data      = createRandomData()
          val signature = SignatureOperations.sign(algorithm, data, originalKey)
          SignatureOperations.verify(algorithm, data, signature, secretKey)

        }
      }
    }
  }
}
