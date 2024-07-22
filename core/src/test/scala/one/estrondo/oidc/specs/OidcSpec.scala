package one.estrondo.oidc.specs

import one.estrondo.oidc.Context
import one.estrondo.oidc.T
import one.estrondo.oidc.TestUnit
import one.estrondo.oidc.syntax._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import scala.util.Failure
import scala.util.Success
import scala.util.Try

object OidcSpec {

  implicit class TryF[A, F[_]](val a: F[A]) extends AnyVal {

    def toTry(implicit ctx: Context[F]): F[Try[A]] = {
      a.map(Success(_)).recover(e => ctx.pure(Failure(e)))
    }
  }
}

abstract class OidcSpec extends AnyFreeSpec with Matchers {

  protected def test(testUnits: TestUnit[T]*): Unit = {
    for (testUnit <- testUnits) {
      testUnit.name in {
        testUnit.unit.run(identity).get
      }
    }
  }
}
