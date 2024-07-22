package one.estrondo.oidc

trait Lookup[F[_], A] {

  def apply(): F[A]

  def invalidate(): F[Unit]
}
