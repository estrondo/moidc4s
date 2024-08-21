package one.estrondo.oidc4s

object Source {

  def of[F[_], A](a: => A): Source[F, A] =
    new Source[F, A] {
      override def apply()(implicit ctx: Context[F]): F[A] = ctx.pure(a)
    }

  def from[F[_], A](a: => F[A]): Source[F, A] =
    new Source[F, A] {
      override def apply()(implicit ctx: Context[F]): F[A] = a
    }
}

trait Source[F[_], A] {

  def apply()(implicit ctx: Context[F]): F[A]
}
