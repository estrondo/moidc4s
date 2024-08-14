package one.estrondo.oidc

import scala.util.Failure
import scala.util.Success
import scala.util.Try

trait T[A] {

  def run[O](out: A => O): Try[O]
}

object T {

  class Pure[A](a: () => A) extends T[A] {
    override def run[O](out: A => O): Try[O] = {
      Success(out(a()))
    }
  }

  class Failed[A](cause: Throwable) extends T[A] {
    override def run[O](out: A => O): Try[O] = {
      Failure(cause)
    }
  }

  class FlatMap[A, B](a: T[A], f: A => T[B]) extends T[B] {
    override def run[O](out: B => O): Try[O] = {
      a.run(f) match {
        case Success(a)     => a.run(out)
        case Failure(cause) => Failure(cause)
      }
    }
  }

  class Map[A, B](a: T[A], f: A => B) extends T[B] {
    override def run[O](out: B => O): Try[O] = {
      a.run(f) match {
        case Success(b)     => Success(out(b))
        case Failure(cause) => Failure(cause)
      }
    }
  }

  class MapError[A](a: T[A], f: Throwable => Throwable) extends T[A] {
    override def run[O](out: A => O): Try[O] = {
      a.run(out) match {
        case Success(o) => Success(o)
        case Failure(c) => Failure(f(c))
      }
    }
  }

  class Recover[A, B >: A](a: T[A], f: Throwable => T[B]) extends T[B] {
    override def run[O](out: B => O): Try[O] = {
      a.run(identity) match {
        case Success(a)     => Success(out(a))
        case Failure(cause) => f(cause).run(out)
      }
    }
  }

  implicit object ContextImpl extends Context[T] {

    override val done: T[Unit] = new Pure(() => ())

    override def pure[A](a: A): T[A] = new Pure(() => a)

    override def failed[A](cause: Throwable): T[A] = new Failed(cause)

    override def flatMap[A, B](a: T[A])(f: A => T[B]): T[B] = new FlatMap(a, f)

    override def map[A, B](a: T[A])(f: A => B): T[B] = new Map(a, f)

    override def mapError[A](a: T[A])(f: Throwable => Throwable): T[A] = new MapError(a, f)

    override def recover[A, B >: A](a: T[A])(f: Throwable => T[B]): T[B] = new Recover(a, f)
  }

  implicit object RefMakerImpl extends Ref.Maker[T] {
    override def make[A](initial: A): T[Ref[T, A]] = new Pure(() => new TRef(initial))
  }
}

class TRef[A](private var current: A) extends Ref[T, A] {

  override def get: T[A] = new T.Pure(() => current)

  override def update(f: A => T[A]): T[Unit] = {
    f(current).run(identity) match {
      case Success(a) =>
        current = a
        T.ContextImpl.done
      case Failure(c) =>
        new T.Failed(c)
    }
  }
}
