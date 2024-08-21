package one.estrondo.oidc4s

import one.estrondo.oidc4s.Transporter.Response

trait Transporter[F[_]] {

  def get(url: String): F[Response]
}

object Transporter {

  @inline def apply[F[_]: Transporter]: Transporter[F] = implicitly[Transporter[F]]

  sealed trait Response

  case class Ok(body: String) extends Response

  case class Failed(status: Int, headers: Map[String, Seq[String]], body: String) extends Response
}
