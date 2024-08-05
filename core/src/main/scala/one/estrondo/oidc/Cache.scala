package one.estrondo.oidc

import syntax._

private[oidc] class Cache[F[_], A] private (ref: Ref[F, Cache.Outcome[A]], lookup: Lookup[F, A]) {

  def get(implicit ctx: Context[F]): F[A] = {
    for {
      outcome <- ref.get
      current <- outcome match {
                   case Cache.Defined(value) => ctx.pure(value)
                   case Cache.Failed(cause)  => ctx.failed(cause)
                   case Cache.NotDefined     => acquire()
                 }
    } yield {
      current
    }
  }

  def invalidate()(implicit ctx: Context[F]): F[Unit] = {
    ref.update {
      case Cache.NotDefined =>
        ctx.pure(Cache.NotDefined)
      case _                =>
        for (_ <- lookup.invalidate()) yield {
          Cache.NotDefined
        }
    }
  }

  private def acquire()(implicit ctx: Context[F]): F[A] = {
    for {
      _       <- ref.update(checkCurrent)
      current <- ref.get.flatMap[A] {
                   case Cache.Defined(a)    => ctx.pure(a)
                   case Cache.Failed(cause) => ctx.failed(cause)
                   case Cache.NotDefined    => ctx.failed(new OidcException.Unexpected("Cache is undefined."))
                 }
    } yield current
  }

  private def checkCurrent(current: Cache.Outcome[A])(implicit ctx: Context[F]): F[Cache.Outcome[A]] = current match {
    case Cache.NotDefined =>
      lookup()
        .map(Cache.Defined.apply)
        .recover(cause => Context[F].pure(Cache.Failed(cause)))
    case _                => ctx.pure(current)
  }
}

private[oidc] object Cache {

  def apply[F[_]: Context: Ref.Maker, A](lookup: Lookup[F, A]): F[Cache[F, A]] =
    for {
      ref <- Ref.maker[F].make[Outcome[A]](NotDefined)
    } yield {
      new Cache(ref, lookup)
    }

  sealed private trait Outcome[+A]

  private case class Defined[A](value: A) extends Outcome[A]

  private case class Failed(cause: Throwable) extends Outcome[Nothing]

  private case object NotDefined extends Outcome[Nothing]
}
