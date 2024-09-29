package one.estrondo.moidc4s

import _root_.cats.effect.IO
import _root_.cats.effect.Resource
import com.dimafeng.testcontainers.WireMockContainer
import one.estrondo.moidc4s.cats.effect._
import one.estrondo.moidc4s.circe._
import one.estrondo.moidc4s.http4s._
import one.estrondo.moidc4s.jwt.Jwt
import one.estrondo.moidc4s.jwt.circe._
import org.http4s.client.Client
import org.http4s.netty.client.NettyClientBuilder
import pdi.jwt.exceptions.JwtValidationException
import weaver.SimpleIOSuite

import javax.crypto.spec.SecretKeySpec
import scala.collection.immutable.HashMap
import scala.util.Failure
import scala.util.Success

object CatsEffectIntegrationSpec extends SimpleIOSuite {

  implicit private def resources: Resource[IO, (Client[IO], WireMockContainer)] =
    for {
      client    <- NettyClientBuilder[IO].resource
      container <- ContainerResource.resourceOf {
                     WireMockContainer
                       .Def()
                       .withMappingFromResource("discovery.json")
                       .withMappingFromResource("jwk_set.json")
                       .start()
                   }
    } yield (client, container)

  test("Using moidc4s with a OpenId Provider through the Discovery Endpoint.")(resources.use {
    case (client, container) =>
      implicit val c: Client[IO] = client
      for {
        provider <- OpenIdProvider(Provider.Discovery(container.getUrl("discovery")))
        result   <-
          provider.evaluate(Jwt.decode())(
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJlc3Ryb25kbyIsIm5hbWUiOiJBbGJlcnQgRWluc3RlaW4iLCJpYXQiOjI1MTc0MzkwMjJ9.qTUxtTGKPg92HpnXm3aeQeZw7ZxH7w_OtBGJ7u6pUrc",
          )
      } yield {
        expect(
          result.hcursor.get[String]("name") == Right("Albert Einstein"),
        )
      }
  })

  test("Using moidc4s with a OpenId Provider through the Discovery Endpoint (invalid token).")(resources.use {
    case (client, container) =>
      implicit val c = client
      for {
        provider <- OpenIdProvider(Provider.Discovery(container.getUrl("discovery")))
        result   <-
          provider
            .evaluate(Jwt.decode())(
              "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJlc3Ryb25kbyIsIm5hbWUiOiJBbGJlcnQgRWluc3RlaW4iLCJpYXQiOjI1MTc0MzkwMjJ9.nhtsqfmFNTOgYxN0OEkpz4-bsmZ4505puxJE8On_97E",
            )
            .redeem(Failure(_), Success(_))
      } yield {
        matches(result) { case Failure(cause) =>
          expect(
            cause.isInstanceOf[
              JwtValidationException,
            ],
          ) and expect(cause.getMessage == "Invalid signature for this token or wrong algorithm.")
        }
      }
  })

  test("Using moidc4s with a OpenId Provider through a provided JwkSet.") {
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
      provider <- OpenIdProvider[IO](Provider.ExternalKeySet(Source.from(IO.pure(externalKeySet))))
      claims   <-
        provider
          .evaluate(Jwt.decode())(
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJlc3Ryb25kbyIsIm5hbWUiOiJBbGJlcnQgRWluc3RlaW4iLCJpYXQiOjI1MTc0MzkwMjJ9.qTUxtTGKPg92HpnXm3aeQeZw7ZxH7w_OtBGJ7u6pUrc",
          )
    } yield {
      expect(
        claims.hcursor.get[String]("name") == Right("Albert Einstein"),
      )
    }
  }
}
