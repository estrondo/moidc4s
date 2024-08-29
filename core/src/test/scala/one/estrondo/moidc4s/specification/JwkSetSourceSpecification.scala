package one.estrondo.moidc4s.specification

import one.estrondo.moidc4s.Context
import one.estrondo.moidc4s.JsonFramework
import one.estrondo.moidc4s.JwkSetFixture
import one.estrondo.moidc4s.JwkSetSource
import one.estrondo.moidc4s.MetadataFixture
import one.estrondo.moidc4s.MockedTestContext
import one.estrondo.moidc4s.Provider
import one.estrondo.moidc4s.TestUnitOps
import one.estrondo.moidc4s.Transporter
import one.estrondo.moidc4s.syntax._
import org.scalamock.clazz.Mock
import org.scalatest.Assertion

//noinspection ConvertExpressionToSAM
class JwkSetSourceSpecification[F[_]: Context] extends TestUnitOps with Mock {

  val u01 = mockedTestUnit[F]("Getting a JwkSet from an Open ID Connect Discovery.")(new DiscoverTestContext {
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

  val u02 = mockedTestUnit[F]("Getting a JwkSet from a JwkSet Uri.")(new JwkSetUriTestContext {
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

  abstract class DiscoverTestContext extends MockedTestContext[F] {
    val url           = "https://estrondo.one/"
    val transporter   = mock[Transporter[F]]
    val jsonFramework = mock[JsonFramework[F]]
    val source        = JwkSetSource(Provider.Discovery(url)(transporter, jsonFramework))
  }

  abstract class JwkSetUriTestContext extends MockedTestContext[F] {
    val url           = "https://estrondo.one/"
    val transporter   = mock[Transporter[F]]
    val jsonFramework = mock[JsonFramework[F]]
    val source        = JwkSetSource(Provider.RemoteJwkSet(url)(transporter, jsonFramework))
  }
}
