package one.estrondo.oidc4s

import _root_.cats.effect.IO
import org.http4s.client.Client
import scala.language.implicitConversions

package object http4s {

  implicit def transporter(client: Client[IO]): Http4sTransporter =
    new Http4sTransporter(client)
}
