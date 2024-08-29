package one.estrondo.moidc4s.zio.http

import com.dimafeng.testcontainers.WireMockContainer
import one.estrondo.moidc4s.Transporter
import one.estrondo.moidc4s.zio.ContainerLayer
import zio.Runtime
import zio.Scope
import zio.ZIO
import zio.ZLayer
import zio.http.Client
import zio.test.TestAspect
import zio.test.TestEnvironment
import zio.test.ZIOSpecDefault
import zio.test.assertTrue
import zio.test.testEnvironment

object HttpTransporterSpec extends ZIOSpecDefault {

  override val bootstrap: ZLayer[Any, Any, TestEnvironment] = Runtime.removeDefaultLoggers >>> testEnvironment

  override def spec = suite("The HttpTransporter:")(
    test("It should respond with Ok when the request returns 2xx.") {
      for {
        container <- ZIO.service[WireMockContainer]
        response  <- ZIO.serviceWithZIO[ZIOHttpTransporter](_.get(container.getUrl("/2xx")))
      } yield {
        assertTrue(response == Transporter.Ok("success!"))
      }
    },
    test("It should respond with an error when the request returns 4xx.") {
      for {
        container <- ZIO.service[WireMockContainer]
        response  <- ZIO.serviceWithZIO[ZIOHttpTransporter](_.get(container.getUrl("/4xx")))
      } yield {
        val failed = response.asInstanceOf[Transporter.Failed]
        assertTrue(
          failed.body == "You should rethink what you want!",
        )
      }
    },
    test("It should respond with an error when the request returns 5xx.") {
      for {
        container <- ZIO.service[WireMockContainer]
        response  <- ZIO.serviceWithZIO[ZIOHttpTransporter](_.get(container.getUrl("/5xx")))
      } yield {
        val failed = response.asInstanceOf[Transporter.Failed]
        assertTrue(
          failed.body == "My bad!",
        )
      }
    },
  ).provide(
    ContainerLayer.layerOf {
      WireMockContainer
        .Def()
        .withMappingFromResource("2xx.json")
        .withMappingFromResource("4xx.json")
        .withMappingFromResource("5xx.json")
        .start()
    },
    Client.default,
    Scope.default,
    ZLayer {
      for {
        client <- ZIO.service[Client]
        scope  <- ZIO.service[Scope]
      } yield {
        transporterFrom(ZLayer.succeed(client) ++ ZLayer.succeed(scope))
      }
    },
  ) @@ TestAspect.sequential
}
