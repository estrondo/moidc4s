package one.estrondo.moidc4s.cats.effect

import _root_.cats.effect.IO
import cats.effect.kernel.Resource
import com.dimafeng.testcontainers.Container

object ContainerResource {

  def resourceOf[R <: Container](container: => R): Resource[IO, R] = {
    Resource.make(IO.blocking {
      container
    })(container =>
      IO.blocking {
        container.stop()
      },
    )
  }
}
