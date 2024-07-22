package one.estrondo.oidc.specs

import java.util.Base64
import one.estrondo.oidc.ECFixture
import one.estrondo.oidc.Jwa
import one.estrondo.oidc.JwaAlg
import one.estrondo.oidc.SignatureOperations
import scala.util.Random
import scala.util.Success

class JwaSpec extends OidcSpec {

  def randomNumber: String = {
    val bytes = Array.ofDim[Byte](32)
    Random.nextBytes(bytes)
    Base64.getUrlEncoder.encodeToString(bytes)
  }

  private def randomData: Array[Byte] = {
    val bytes = Array.ofDim[Byte](1024 * 1024)
    Random.nextBytes(bytes)
    bytes
  }

  "Jwa should" - {

    "read EC key" in {

      val (p1, p2, jwk) = ECFixture.createRandomP521(Some(JwaAlg.Es512))
      val algorithm     = JwaAlg.read(jwk).get

      Jwa(
        "EC",
        jwk,
      ) shouldBe a[Success[_]]

      val data      = randomData
      val signature = SignatureOperations.sign(algorithm, data, p2)
      SignatureOperations.verify(algorithm, data, signature, p1) should be(true)
    }
  }
}
