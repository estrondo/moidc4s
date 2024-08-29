package one.estrondo.moidc4s.zio

import scala.language.implicitConversions
import zio.Scope
import zio.TaskLayer
import zio.http.Client

package object http {

  implicit def transporterFrom(implicit layer: TaskLayer[Client with Scope]): ZIOHttpTransporter =
    new ZIOHttpTransporter(layer)
}
