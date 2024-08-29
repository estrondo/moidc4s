package one.estrondo.moidc4s

private[moidc4s] trait Lookup[F[_], A] {

  def apply()(implicit ctx: Context[F]): F[A]

  def invalidate()(implicit ctx: Context[F]): F[Unit]
}
