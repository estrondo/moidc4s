package one.estrondo.oidc.specification

import one.estrondo.oidc.Context
import one.estrondo.oidc.Json
import one.estrondo.oidc.JwkSetFixture
import one.estrondo.oidc.LookupJwkSet
import one.estrondo.oidc.MetadataFixture
import one.estrondo.oidc.MockedContext
import one.estrondo.oidc.Provider
import one.estrondo.oidc.TestUnitOps
import one.estrondo.oidc.Transporter
import one.estrondo.oidc.syntax._

class LookupJwkSetSpecification[F[_]: Context] extends TestUnitOps {

  val u01 = testUnit("It should extract a JwkSet from an OpenID Discovery endpoint.")(new FromDiscoveryTestContext {
    def run = {
      val expectedMetadata = MetadataFixture.createRandom()
      val expectedJwkSet   = JwkSetFixture.createRandom()

      (transporter.get _)
        .expects(expectedUrl)
        .returning(pureF(Transporter.Ok("valid-metadata")))
        .once()

      (json.metadata _)
        .expects("valid-metadata")
        .returning(pureF(expectedMetadata))
        .once()

      (transporter.get _)
        .expects(expectedMetadata.jwks_uri.get)
        .returning(pureF(Transporter.Ok("valid-jwkset")))
        .once()

      (json.jwkSet _)
        .expects("valid-jwkset")
        .returning(pureF(expectedJwkSet))
        .once()

      for (result <- lookup()) yield {
        result should be(expectedJwkSet)
      }
    }
  }.verified)

  val u02 = testUnit("It should extract a JwkSet from an JwkSet's url.")(new FromRemoteJwkSetTestContext {
    def run = {
      val expectedJwkSet = JwkSetFixture.createRandom()

      (transporter.get _)
        .expects(expectedUrl)
        .returning(pureF(Transporter.Ok("valid-jwkset")))
        .once()

      (json.jwkSet _)
        .expects("valid-jwkset")
        .returning(pureF(expectedJwkSet))
        .once()

      for (result <- lookup()) yield {
        result should be(expectedJwkSet)
      }
    }
  }.verified)

  abstract private class FromRemoteTestContext extends MockedContext[F] {

    protected type P <: Provider.RemoteJwkSetProvider[F]

    val provider: P

    implicit val transporter: Transporter[F] = mock[Transporter[F]]
    implicit val json: Json[F]               = mock[Json[F]]

    def lookup = new LookupJwkSet.FromRemote[F](provider)
  }

  abstract private class FromDiscoveryTestContext extends FromRemoteTestContext {

    override type P = Provider.Discovery[F]
    val expectedUrl          = "https://estrondo.one/.well-know/openid"
    override val provider: P = Provider.Discovery(expectedUrl)
  }

  abstract private class FromRemoteJwkSetTestContext extends FromRemoteTestContext {
    override type P = Provider.JwkSetUri[F]
    val expectedUrl                              = "https://estrondo.one/jwkset"
    override val provider: Provider.JwkSetUri[F] = Provider.JwkSetUri(expectedUrl)
  }
}
