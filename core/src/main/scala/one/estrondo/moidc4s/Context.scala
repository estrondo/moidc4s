package one.estrondo.moidc4s

import scala.util.Try

trait Context[F[_]] {

  def attempt[A](a: => A): F[A] = {
    try {
      pure(a)
    } catch {
      case cause: Throwable => failed(cause)
    }
  }

  def pure[A](a: A): F[A]

  def flatMap[A, B](a: F[A])(f: A => F[B]): F[B]

  def failed[A](cause: Throwable): F[A]

  def map[A, B](a: F[A])(f: A => B): F[B]

  def mapError[A](a: F[A])(f: Throwable => Throwable): F[A]

  def done: F[Unit]

  def recover[A, B >: A](a: F[A])(f: Throwable => F[B]): F[B]

  def fromTry[A](a: => Try[A]): F[A]

}

object Context {

  @inline def apply[F[_]: Context]: Context[F] = implicitly[Context[F]]
}
