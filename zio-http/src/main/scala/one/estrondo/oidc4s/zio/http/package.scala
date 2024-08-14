package one.estrondo.oidc4s.zio

import zio.Scope
import zio.ZIO
import zio.http.Client

package object http {
  type OHttp[A] = ZIO[Client with Scope, Throwable, A]
}
