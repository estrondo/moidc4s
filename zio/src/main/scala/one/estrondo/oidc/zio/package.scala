package one.estrondo.oidc

import _root_.zio.{Ref => ZRef}
import _root_.zio.ZIO

package object zio {

  type OZIO[A] = ZIO[Any, Throwable, A]

  implicit object ZIOContext extends Context[OZIO] {
    override val done: OZIO[Unit] =
      ZIO.succeed(())

    override def pure[A](a: A): OZIO[A] =
      ZIO.succeed(a)

    override def flatMap[A, B](a: OZIO[A])(f: A => OZIO[B]): OZIO[B] =
      a.flatMap(f)

    override def failed[A](cause: Throwable): OZIO[A] =
      ZIO.fail(cause)

    override def map[A, B](a: OZIO[A])(f: A => B): OZIO[B] =
      a.map(f)

    override def mapError[A](a: OZIO[A])(f: Throwable => Throwable): OZIO[A] =
      a.mapError(f)

    override def recover[A, B >: A](a: OZIO[A])(f: Throwable => OZIO[B]): OZIO[B] =
      a.catchAll(f)
  }

  implicit object ZIORefMaker extends Ref.Maker[OZIO] {
    override def make[A](initial: A): OZIO[Ref[OZIO, A]] =
      for {
        ref <- ZRef.Synchronized.make(initial)
      } yield {
        new ZioRef(ref)
      }
  }
}
