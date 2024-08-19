package one.estrondo.oidc4s.http4s

import _root_.cats.effect.IO
import one.estrondo.oidc.Transporter
import org.http4s.Response
import org.http4s.Status
import org.http4s.client.Client

class Http4sTransporter(client: Client[IO]) extends Transporter[IO] {

  override def get(url: String): IO[Transporter.Response] = {
    client.get(url) {
      case Status.Successful(response)    => ok(response)
      case Status.ClientError(response)   => clientError(response)
      case Status.ServerError(response)   => serverError(response)
      case Status.Redirection(response)   => redirection(response)
      case Status.Informational(response) => informational(response)
    }
  }

  private def ok(response: Response[IO]): IO[Transporter.Ok] = {
    for (body <- response.as[String]) yield {
      Transporter.Ok(body)
    }
  }

  private def clientError(response: Response[IO]): IO[Transporter.Failed] = {
    for (body <- response.as[String]) yield {
      Transporter.Failed(
        status = response.status.code,
        headers = response.headers.headers.groupBy(_.name.toString).map(x => (x._1, x._2.map(_.value))),
        body = body,
      )
    }
  }

  private def serverError(response: Response[IO]): IO[Transporter.Failed] =
    clientError(response)

  private def redirection(response: Response[IO]): IO[Transporter.Response] =
    clientError(response)

  private def informational(response: Response[IO]): IO[Transporter.Failed] =
    clientError(response)
}
