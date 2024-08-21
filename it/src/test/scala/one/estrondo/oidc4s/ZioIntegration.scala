package one.estrondo.oidc4s

import _root_.zio.Scope
import _root_.zio.TaskLayer
import _root_.zio.ZIO
import _root_.zio.ZLayer
import _root_.zio.http.Client
import _root_.zio.json.ast.Json.Obj
import _root_.zio.json.ast.Json.Str
import _root_.zio.test._
import _root_.zio.test.TestAspect
import _root_.zio.test.ZIOSpecDefault
import com.dimafeng.testcontainers.WireMockContainer
import javax.crypto.spec.SecretKeySpec
import one.estrondo.oidc4s.jwt.Jwt
import one.estrondo.oidc4s.jwt.zio._
import one.estrondo.oidc4s.zio._
import one.estrondo.oidc4s.zio.http._
import one.estrondo.oidc4s.zio.json._
import pdi.jwt.exceptions.JwtValidationException
import scala.collection.immutable.HashMap

object ZioIntegration extends ZIOSpecDefault {

  private val httpLayer: TaskLayer[Client with Scope with WireMockContainer] =
    Scope.default >+> (Client.default ++ ContainerLayer.layerOf {
      WireMockContainer
        .Def()
        .withMappingFromResource("discovery.json")
        .withMappingFromResource("jwk_set.json")
        .start()
    })

  override def spec = suite("ZIO Integration")(
    test("Creating a oidc4s provider with implicit dependencies. ") {
      for {
        client   <- ZIO.service[Client]
        scope    <- ZIO.service[Scope]
        provider <- {
          implicit val layer: TaskLayer[Client with Scope] = ZLayer.succeed(client) ++ ZLayer.succeed(scope)
          OpenIdProvider[OZIO](Provider.Discovery("http://localhost"))
        }

      } yield {
        assertTrue(provider != null)
      }
    }.provideSome(httpLayer),
    test("Using oidc4s with a OpenId Provider through the Discovery Endpoint.") {
      for {
        client            <- ZIO.service[Client]
        scope             <- ZIO.service[Scope]
        wireMockContainer <- ZIO.service[WireMockContainer]
        provider          <- {
          implicit val layer: TaskLayer[Client with Scope] = ZLayer.succeed(client) ++ ZLayer.succeed(scope)
          OpenIdProvider[OZIO](Provider.Discovery(wireMockContainer.getUrl("discovery")))
        }

        claims <-
          provider.evaluate(Jwt.decode())(
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJlc3Ryb25kbyIsIm5hbWUiOiJBbGJlcnQgRWluc3RlaW4iLCJpYXQiOjI1MTc0MzkwMjJ9.qTUxtTGKPg92HpnXm3aeQeZw7ZxH7w_OtBGJ7u6pUrc",
          )
      } yield {
        val Obj(fields)     = claims
        val Some(Str(name)) = fields.toMap.get("name")
        assertTrue(name == "Albert Einstein")
      }
    }.provideSome(httpLayer),
    test("Using oidc4s with a OpenId Provider through the Discovery Endpoint (invalid token).") {
      for {
        client            <- ZIO.service[Client]
        scope             <- ZIO.service[Scope]
        wireMockContainer <- ZIO.service[WireMockContainer]
        provider          <- {
          implicit val layer: TaskLayer[Client with Scope] = ZLayer.succeed(client) ++ ZLayer.succeed(scope)
          OpenIdProvider[OZIO](Provider.Discovery(wireMockContainer.getUrl("discovery")))
        }

        exit <-
          provider
            .evaluate(Jwt.decode())(
              "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJlc3Ryb25kbyIsIm5hbWUiOiJBbGJlcnQgRWluc3RlaW4iLCJpYXQiOjI1MTc0MzkwMjJ9.nhtsqfmFNTOgYxN0OEkpz4-bsmZ4505puxJE8On_97E",
            )
            .exit
      } yield {
        assertTrue(
          exit.is(_.failure).isInstanceOf[JwtValidationException],
          exit.is(_.failure).getMessage == "Invalid signature for this token or wrong algorithm.",
        )
      }
    }.provideSome(httpLayer),
    test("Providing an external KeySet") {

      val description    = KeyDescription(
        kid = Some("00998877665544332211"),
        alg = Some(JwaAlgorithm.Hs256),
        key = KeyDescription.Secret(
          new SecretKeySpec(
            B64.UrlDecoder.decode("YSBzdXBlciBzZWNyZXQgcGFzc3dvcmQ="),
            JwaAlgorithm.Hs256.fullName,
          ),
        ),
      )
      val externalKeySet = KeySet(
        byKid = HashMap(
          "00998877665544332211" -> description,
        ),
        withoutKid = Seq.empty,
      )

      for {
        provider <- OpenIdProvider[OZIO](Provider.ExternalKeySet(Source.from(ZIO.succeed(externalKeySet))))
        claims   <-
          provider
            .evaluate(Jwt.decode())(
              "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJlc3Ryb25kbyIsIm5hbWUiOiJBbGJlcnQgRWluc3RlaW4iLCJpYXQiOjI1MTc0MzkwMjJ9.qTUxtTGKPg92HpnXm3aeQeZw7ZxH7w_OtBGJ7u6pUrc",
            )
      } yield {
        val Obj(fields)     = claims
        val Some(Str(name)) = fields.toMap.get("name")
        assertTrue(name == "Albert Einstein")
      }
    },
  ) @@ TestAspect.sequential
}
