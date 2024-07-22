package one.estrondo.oidc

import scala.util.Success
import scala.util.Try

package object syntax {
  implicit class SyntaxContext[A, F[_]](val a: F[A]) extends AnyVal {

    def flatMap[B](f: A => F[B])(implicit ctx: Context[F]): F[B] =
      ctx.flatMap(a)(f)

    def map[B](f: A => B)(implicit ctx: Context[F]): F[B] =
      ctx.map(a)(f)

    def mapError(f: Throwable => Throwable)(implicit ctx: Context[F]): F[A] =
      ctx.mapError(a)(f)

    def recover[B >: A](f: Throwable => F[B])(implicit ctx: Context[F]): F[B] = {
      ctx.recover[A, B](a)(f)
    }
  }

  implicit class RichIterable[A](val i: Iterable[A]) extends AnyVal {
    def tryFoldLeft[B](initial: B)(op: (B, A) => Try[B]): Try[B] = {
      var current: Try[B] = Success(initial)
      val iterator        = i.iterator
      while (iterator.hasNext && current.isSuccess) {
        current = op(current.get, iterator.next())
      }

      current
    }
  }
}
