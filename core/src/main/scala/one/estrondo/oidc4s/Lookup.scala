package one.estrondo.oidc4s

private[oidc4s] trait Lookup[F[_], A] {

  def apply()(implicit ctx: Context[F]): F[A]

  def invalidate()(implicit ctx: Context[F]): F[Unit]
}
