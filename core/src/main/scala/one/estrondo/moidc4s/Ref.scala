package one.estrondo.moidc4s

trait Ref[F[_], A] {

  def get: F[A]

  def update(f: A => F[A]): F[Unit]
}

object Ref {

  trait Maker[F[_]] {

    def make[A](initial: A): F[Ref[F, A]]
  }

  def maker[F[_]: Maker]: Maker[F] = implicitly[Maker[F]]

}
