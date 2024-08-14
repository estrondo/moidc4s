package one.estrondo.oidc.specification

import one.estrondo.oidc.Base64Ops._
import one.estrondo.oidc.Context
import one.estrondo.oidc.HeaderExtractor
import one.estrondo.oidc.JsonFramework
import one.estrondo.oidc.JwtHeaderFixture
import one.estrondo.oidc.MockedTestContext
import one.estrondo.oidc.TestUnitOps
import one.estrondo.oidc.syntax._
import org.scalatest.Assertion

class HeaderExtractorSpecification[F[_]: Context] extends TestUnitOps {

  val u01 = mockedTestUnit[F]("It should extract a header properly.")(new C {

    override def apply(): F[Assertion] = {
      val expectedHeader      = JwtHeaderFixture.createRandom()
      val expectedTokenHeader = """{"typ":"JWT"}"""
      val expectedToken       =
        s"""${encodeBase64UrlEncoded(expectedTokenHeader)}.${encodeBase64UrlEncoded("""{"iss":"@"}""")}"""

      (jsonFramework.jwtHeader _)
        .expects(expectedTokenHeader)
        .returning(pureF(expectedHeader))
        .once()

      for (result <- HeaderExtractor(expectedToken)) yield {
        result should be(expectedHeader)
      }
    }
  })

  abstract class C extends MockedTestContext[F] {
    implicit val jsonFramework: JsonFramework[F] = mock[JsonFramework[F]]
  }
}
