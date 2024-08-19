package one.estrondo.oidc4s.cats

import cats.effect.IO
import cats.effect.std.AtomicCell
import one.estrondo.oidc.Context
import one.estrondo.oidc.Ref
import scala.util.Try

package object effect {

  implicit object CatsEffect extends Context[IO] {

    override def pure[A](a: A): IO[A] =
      IO.pure(a)

    override def flatMap[A, B](a: IO[A])(f: A => IO[B]): IO[B] =
      a.flatMap(f)

    override def failed[A](cause: Throwable): IO[A] =
      IO.raiseError(cause)

    override def map[A, B](a: IO[A])(f: A => B): IO[B] =
      a.map(f)

    override def mapError[A](a: IO[A])(f: Throwable => Throwable): IO[A] =
      a.handleErrorWith[A](cause => IO.raiseError(f(cause)))

    override def done: IO[Unit] =
      IO.unit

    override def recover[A, B >: A](a: IO[A])(f: Throwable => IO[B]): IO[B] =
      a.recoverWith { case cause: Throwable => f(cause) }

    override def fromTry[A](a: => Try[A]): IO[A] =
      IO.defer(IO.fromTry(a))
  }

  implicit object CatsEffectRefMaker extends Ref.Maker[IO] {

    override def make[A](initial: A): IO[Ref[IO, A]] = {
      for {
        atomicCell <- AtomicCell[IO].of(initial)
      } yield new AtomicCellRef(atomicCell)
    }
  }
}
