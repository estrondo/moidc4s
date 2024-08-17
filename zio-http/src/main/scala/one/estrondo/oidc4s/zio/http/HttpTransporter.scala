package one.estrondo.oidc4s.zio.http

import java.nio.charset.StandardCharsets
import one.estrondo.oidc.Transporter
import zio.http.Client
import zio.http.Request

object HttpTransporter extends Transporter[HttpZIO] {

  override def get(url: String): HttpZIO[Transporter.Response] = {
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
