package one.estrondo.oidc.specification

import one.estrondo.oidc.Context
import one.estrondo.oidc.JsonFramework
import one.estrondo.oidc.JwkSetFixture
import one.estrondo.oidc.JwkSetSource
import one.estrondo.oidc.MetadataFixture
import one.estrondo.oidc.MockedContext
import one.estrondo.oidc.Provider
import one.estrondo.oidc.TestUnitOps
import one.estrondo.oidc.Transporter
import one.estrondo.oidc.syntax._
import org.scalamock.clazz.Mock
import org.scalatest.Assertion

//noinspection ConvertExpressionToSAM
class JwkSetSourceSpecification[F[_]: Context] extends TestUnitOps with Mock {

  val u01 = mockedTestUnit[F]("Getting a JwkSet from an Open ID Connect Discovery.")(new DiscoverContext {
    override def apply(): F[Assertion] = {
      (transporter.get _)
        .expects(url)
        .returning(pureF(Transporter.Ok("VALID METADATA!")))
        .once()

      val expectedJwkSet   = JwkSetFixture.createRandom()
      val expectedMetadata = MetadataFixture.createRandom()

      (jsonFramework.metadata _)
        .expects("VALID METADATA!")
        .returning(pureF(expectedMetadata))
        .once()

      (transporter.get _)
        .expects(expectedMetadata.jwks_uri.get)
        .returning(pureF(Transporter.Ok("VALID JWKSet!")))
        .once()

      (jsonFramework.jwkSet _)
        .expects("VALID JWKSet!")
        .returning(pureF(expectedJwkSet))

      for (jwk <- source()) yield {
        jwk should be(expectedJwkSet)
      }
    }
  })

  val u02 = mockedTestUnit[F]("Getting a JwkSet from a JwkSet Uri.")(new JwkSetUriContext {
    override def apply(): F[Assertion] = {
      (transporter.get _)
        .expects(url)
        .returning(pureF(Transporter.Ok("VALID JwkSet!")))
        .once()

      val expectedJwkSet = JwkSetFixture.createRandom()

      (jsonFramework.jwkSet _)
        .expects("VALID JwkSet!")
        .returning(pureF(expectedJwkSet))
        .once()

      for (jwkSet <- source()) yield {
        jwkSet should be(expectedJwkSet)
      }
    }
  })

  abstract class DiscoverContext extends MockedContext[F] {
    val url           = "https://estrondo.one/"
    val transporter   = mock[Transporter[F]]
    val jsonFramework = mock[JsonFramework[F]]
    val source        = JwkSetSource(Provider.Discovery(url)(transporter, jsonFramework))
  }

  abstract class JwkSetUriContext extends MockedContext[F] {
    val url           = "https://estrondo.one/"
    val transporter   = mock[Transporter[F]]
    val jsonFramework = mock[JsonFramework[F]]
    val source        = JwkSetSource(Provider.RemoteJwkSet(url)(transporter, jsonFramework))
  }
}
