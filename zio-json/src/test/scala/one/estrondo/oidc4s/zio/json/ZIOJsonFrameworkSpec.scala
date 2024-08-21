package one.estrondo.oidc4s.zio.json

import one.estrondo.oidc4s.JwkSet
import one.estrondo.oidc4s.zio.ZioOidcSpec
import zio.Scope
import zio.stream.ZStream
import zio.test.Spec
import zio.test.TestEnvironment
import zio.test.TestResult
import zio.test.assertTrue

object ZIOJsonFrameworkSpec extends ZioOidcSpec {

  def validateMetadata(name: String, resource: String, expected: String): Spec[Any, Throwable] =
    test(s"It should read a Metadata [$name].") {
      for {
        data     <- ZStream.fromResource(resource).runCollect.map(_.asString)
        metadata <- ZIOJsonFramework.metadata(data)
      } yield {
        assertTrue(metadata.jwks_uri.contains(expected))
      }
    }

  def validateJwkSet(name: String, resource: String, validator: JwkSet => TestResult): Spec[Any, Throwable] =
    test(s"It should read a JwkSet [$name].") {
      for {
        data   <- ZStream.fromResource(resource).runCollect.map(_.asString)
        jwkSet <- ZIOJsonFramework.jwkSet(data)
      } yield validator(jwkSet)
    }

  def validateJwtHeader(resource: String, alg: Option[String], kid: Option[String]): Spec[Any, Throwable] =
    test(s"It should read a JWT Header[$resource].") {
      for {
        data   <- ZStream.fromResource(resource).runCollect.map(_.asString)
        header <- ZIOJsonFramework.jwtHeader(data)
      } yield {
        assertTrue(
          header.alg == alg,
          header.kid == kid,
        )
      }
    }

  override def spec: Spec[TestEnvironment with Scope, Any] = suite("ZIOJsonFramework")(
    validateMetadata(
      "Google",
      "google.open-id-configuration.json",
      "https://www.googleapis.com/oauth2/v3/certs",
    ),
    validateMetadata(
      "Microsoft",
      "microsoft.open-id-configuration.json",
      "https://login.microsoftonline.com/consumers/discovery/v2.0/keys",
    ),
    validateJwkSet(
      "Google",
      "google.jwkset.json",
      jwkSet =>
        assertTrue(
          jwkSet.keys.size == 2,
        ),
    ),
    validateJwkSet(
      "Microsoft",
      "microsoft.jwkset.json",
      jwKSet =>
        assertTrue(
          jwKSet.keys.size == 7,
        ),
    ),
    validateJwtHeader("jwt.01.json", alg = Some("HS256"), kid = None),
    validateJwtHeader("jwt.02.json", alg = Some("HS512"), kid = Some("0a0b0c0d0f")),
  )
}
