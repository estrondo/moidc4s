package one.estrondo.oidc.specs

import one.estrondo.oidc.Base64Ops
import one.estrondo.oidc.ECFixture
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
      SignatureOperations.verify(algorithm, data, signature, publicKey) should be(true)
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
      SignatureOperations.verify(algorithm, data, signature, publicKey) should be(true)
    }
  }
}
