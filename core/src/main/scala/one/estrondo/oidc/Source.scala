package one.estrondo.oidc

trait Source[F[_], A] {

  def apply()(implicit ctx: Context[F]): F[A]
}
