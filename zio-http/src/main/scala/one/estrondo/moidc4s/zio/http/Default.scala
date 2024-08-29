package one.estrondo.moidc4s.zio.http
import _root_.zio.Scope
import _root_.zio.http.Client
import zio.TaskLayer

//noinspection ScalaWeakerAccess
trait Default {

  implicit def defaultHttpLayer: TaskLayer[Client with Scope] = Client.default ++ Scope.default
}

object Default extends Default
