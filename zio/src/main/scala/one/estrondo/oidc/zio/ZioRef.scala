package one.estrondo.oidc.zio

import _root_.zio.{Ref => ZRef}
import one.estrondo.oidc.Ref

class ZioRef[A](ref: ZRef.Synchronized[A]) extends Ref[OZIO, A] {

  override def get: OZIO[A] =
    ref.get

  override def update(f: A => OZIO[A]): OZIO[Unit] =
    ref.updateZIO(f)
}
