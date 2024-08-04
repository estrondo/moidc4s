package one.estrondo.oidc.specs

import one.estrondo.oidc.Base64Ops
import one.estrondo.oidc.ECFixture
import one.estrondo.oidc.Jwa
import one.estrondo.oidc.JwaAlg
import one.estrondo.oidc.SignatureOperations
import scala.util.Random
import scala.util.Success

class JwaSpec extends OidcSpec with Base64Ops {

  private def randomData: Array[Byte] = {
    val bytes = Array.ofDim[Byte](1024 * 32)
    Random.nextBytes(bytes)
    bytes
  }

  "Jwa should" - {

    "read an valid EC Key." in {

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
