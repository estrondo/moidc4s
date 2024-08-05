package one.estrondo.oidc

private[oidc] trait Lookup[F[_], A] {

  def apply()(implicit ctx: Context[F]): F[A]

  def invalidate()(implicit ctx: Context[F]): F[Unit]
}
