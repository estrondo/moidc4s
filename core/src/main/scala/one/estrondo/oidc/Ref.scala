package one.estrondo.oidc

trait Ref[F[_], A] {

  def get: F[A]

  def update(f: A => F[A]): F[Unit]
}

trait RefMaker[F[_]] {

  def make[A](initial: A): F[Ref[F, A]]
}

object RefMaker {

  @inline def apply[F[_]: RefMaker]: RefMaker[F] = implicitly[RefMaker[F]]
}
