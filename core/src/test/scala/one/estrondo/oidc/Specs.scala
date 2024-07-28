package one.estrondo.oidc

import org.scalatest.matchers.should.Matchers

trait Specs[F[_]] extends Matchers {

  def pureF[A](a: A)(implicit c: Context[F]): F[A] = c.pure(a)
}
