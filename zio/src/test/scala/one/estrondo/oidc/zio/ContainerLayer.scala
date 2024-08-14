package one.estrondo.oidc.zio

import com.dimafeng.testcontainers.Container
import zio.Scope
import zio.Tag
import zio.ZIO
import zio.ZLayer

object ContainerLayer {

  def layerOf[R <: Container: Tag](container: => R): ZLayer[Scope, Throwable, R] = {
    ZLayer.fromZIO {
      ZIO.acquireRelease(ZIO.attemptBlocking {
        container
      })(container =>
        ZIO.attemptBlocking {
          container.stop()
        }.orDie,
      )
    }
  }
}
