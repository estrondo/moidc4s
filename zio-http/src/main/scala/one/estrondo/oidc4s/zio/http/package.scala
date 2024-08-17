package one.estrondo.oidc4s.zio

import zio.Scope
import zio.ZIO
import zio.http.Client

package object http {
  type HttpZIO[A] = ZIO[Client with Scope, Throwable, A]
}
