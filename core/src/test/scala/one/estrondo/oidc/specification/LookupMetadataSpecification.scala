package one.estrondo.oidc.specification

import one.estrondo.oidc.Context
import one.estrondo.oidc.Json
import one.estrondo.oidc.LookupMetadata
import one.estrondo.oidc.MetadataFixture
import one.estrondo.oidc.MockedContext
import one.estrondo.oidc.OidcException
import one.estrondo.oidc.Provider
import one.estrondo.oidc.TestUnit
import one.estrondo.oidc.Transporter
import one.estrondo.oidc.specs.OidcSpec
import one.estrondo.oidc.specs.OidcSpec.TryF
import one.estrondo.oidc.syntax._
import org.scalatest.TryValues
import org.scalatest.matchers.should.Matchers
import scala.util.Failure

class LookupMetadataSpecification[F[_]: Context] extends OidcSpec with TryValues {

  protected val expectedDiscoveryUrl                 = "http://estrondo.one/.well-known/openid-configuration"
  protected val expectedProvider: Provider.Discovery = Provider.Discovery(url = expectedDiscoveryUrl)

  def u01 = TestUnit(
    name = "LookupMetadata: it should parse correctly a OpenId Configuration.",
    unit = new UnitContext(expectedProvider) {

      def run = {
        val expectedMetadata = MetadataFixture.createRandom()

        (transporter.get _)
          .expects(expectedDiscoveryUrl)
          .returning(Context[F].pure(Transporter.Ok("Valid JSON")))
          .once()

        (json.metadata _)
          .expects("Valid JSON")
          .returning(Context[F].pure(expectedMetadata))
          .once()

        for (result <- lookup()) yield {
          result should be(expectedMetadata)
        }
      }
    }.verified,
  )

  def u02 = TestUnit(
    name = "LookupMetadata: when it receives an invalid JSON.",
    unit = new UnitContext(expectedProvider) {
      def run = {
        (transporter.get _)
          .expects(expectedDiscoveryUrl)
          .returning(Context[F].pure(Transporter.Ok("Invalid JSON")))
          .once()

        (json.metadata _)
          .expects("Invalid JSON")
          .returning(Context[F].failed(new IllegalArgumentException("!!!")))

        for {
          response <- lookup().toTry
        } yield {
          response shouldBe a[Failure[_]]
        }
      }
    }.verified,
  )

  def u03 = TestUnit(
    name = "LookupMetadata: when it receives a invalid HTTP response.",
    unit = new UnitContext(expectedProvider) {
      def run = {
        val expectedResponse = Transporter.Failed(
          status = 400,
          headers = Map.empty,
          body = "",
        )

        (transporter.get _)
          .expects(expectedDiscoveryUrl)
          .returning(Context[F].pure(expectedResponse))
          .once()

        for (response <- lookup().toTry) yield {
          response.failure.exception shouldBe an[OidcException.FailedRequest]
        }
      }
    }.verified,
  )

  def u04 = TestUnit(
    name = "LookupMetadata: When the Transporter fails it should report.",
    unit = new UnitContext(expectedProvider) {
      def run = {
        (transporter.get _)
          .expects(expectedDiscoveryUrl)
          .returning(Context[F].failed(new IllegalStateException("@@@")))
          .once()

        for (result <- lookup().toTry) yield {
          result.failure.exception shouldBe an[OidcException.Unexpected]
        }
      }
    }.verified,
  )

  abstract class UnitContext(provider: Provider) extends MockedContext[F] with Matchers {
    implicit val transporter: Transporter[F] = mock[Transporter[F]]
    implicit val json: Json[F]               = mock[Json[F]]
    val lookup                               = new LookupMetadata[F](provider)
  }

}
