package one.estrondo.oidc4s.zio.http

import java.nio.charset.StandardCharsets
import one.estrondo.oidc4s.Transporter
import one.estrondo.oidc4s.zio.OZIO
import zio.Scope
import zio.TaskLayer
import zio.http.Client
import zio.http.Request

class ZIOHttpTransporter(layer: TaskLayer[Client with Scope]) extends Transporter[OZIO] {

  override def get(url: String): OZIO[Transporter.Response] = {
    _get(url).provideLayer(layer)
  }

  private def _get(url: String) = {
    for {
      response <- Client.request(Request.get(url))
      body     <- response.body.asString(StandardCharsets.UTF_8)
    } yield {
      if (response.status.isSuccess) {
        Transporter.Ok(body)
      } else {
        Transporter.Failed(
          status = response.status.code,
          headers = for ((headerName, values) <- response.headers.groupBy(_.headerName)) yield {
            headerName -> values.toSeq.map(_.renderedValue)
          },
          body = body,
        )
      }
    }
  }
}
