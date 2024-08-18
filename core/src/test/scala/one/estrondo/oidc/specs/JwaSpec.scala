package one.estrondo.oidc.specs

import one.estrondo.oidc.ECFixture
import one.estrondo.oidc.HMacFixture
import one.estrondo.oidc.Jwa
import one.estrondo.oidc.JwaAlgorithm
import one.estrondo.oidc.KeyDescription
import one.estrondo.oidc.RSAFixture
import one.estrondo.oidc.SignatureOperations
import scala.util.Random
import scala.util.Success

class JwaSpec extends OidcSpec {

  private def createRandomData(): Array[Byte] = {
    val bytes = Array.ofDim[Byte](1024 * 32)
    Random.nextBytes(bytes)
    bytes
  }

  "Jwa should" - {

    "read a valid EC Key." - {

      for (
        input <- Seq(
                   JwaAlgorithm.Es256,
                   JwaAlgorithm.Es384,
                   JwaAlgorithm.Es512,
                 )
      ) {
        input.name in {
          val (_, privateKey, jwk) = ECFixture.createRandom(input)
          val Success(description) = Jwa(
            "EC",
            jwk,
          )

          val KeyDescription.Public(publicKey) = description.key
          val Some(algorithm)                  = description.alg
          val data                             = createRandomData()
          val signature                        = SignatureOperations.sign(input, data, privateKey)

          SignatureOperations.verify(algorithm, data, signature, publicKey)
        }
      }
    }

    "read a valid RSA Key" - {

      for (
        input <- Seq(
                   JwaAlgorithm.Rs256,
                   JwaAlgorithm.Rs384,
                   JwaAlgorithm.Rs512,
                 )
      ) {
        input.name in {
          val (_, privateKey, jwk) = RSAFixture.createRandom(input)
          val Success(description) = Jwa(
            "RSA",
            jwk,
          )

          val KeyDescription.Public(publicKey) = description.key
          val Some(algorithm)                  = description.alg
          val data                             = createRandomData()
          val signature                        = SignatureOperations.sign(input, data, privateKey)

          SignatureOperations.verify(algorithm, data, signature, publicKey)
        }
      }

    }

    "read a valid Hmac* key" - {
      for {
        input <- Seq(
                   JwaAlgorithm.Hs256,
                   JwaAlgorithm.Hs384,
                   JwaAlgorithm.Hs512,
                 )
      } {
        input.name in {
          val (originalKey, jwk) = HMacFixture.createRandom(input)

          val Success(description)             = Jwa("oct", jwk)
          val KeyDescription.Secret(secretKey) = description.key
          val Some(algorithm)                  = description.alg
          val data                             = createRandomData()
          val signature                        = SignatureOperations.sign(algorithm, data, originalKey)

          SignatureOperations.verify(algorithm, data, signature, secretKey)
        }
      }
    }
  }
}
