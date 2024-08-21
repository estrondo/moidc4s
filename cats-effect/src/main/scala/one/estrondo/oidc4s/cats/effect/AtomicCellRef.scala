package one.estrondo.oidc4s.cats.effect

import cats.effect.IO
import cats.effect.std.AtomicCell
import one.estrondo.oidc4s.Ref

class AtomicCellRef[A](atomicCell: AtomicCell[IO, A]) extends Ref[IO, A] {

  override def get: IO[A] = atomicCell.get

  override def update(f: A => IO[A]): IO[Unit] = atomicCell.evalUpdate(f)

}
