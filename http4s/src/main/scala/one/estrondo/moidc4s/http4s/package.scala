package one.estrondo.moidc4s

import _root_.cats.effect.IO
import org.http4s.client.Client

package object http4s {

  implicit def transporter(implicit client: Client[IO]): Http4sTransporter =
    new Http4sTransporter(client)
}
