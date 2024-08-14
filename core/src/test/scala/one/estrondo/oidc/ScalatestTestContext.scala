package one.estrondo.oidc

import one.estrondo.oidc.syntax._
import org.scalatest.OptionValues
import org.scalatest.TryValues
import org.scalatest.matchers.should.Matchers
import scala.util.Failure
import scala.util.Success
import scala.util.Try

trait ScalatestTestContext[F[_]] extends Matchers with TryValues with OptionValues {

  implicit class FTry[A](a: F[A]) {
    def toTry(implicit ctx: Context[F]): F[Try[A]] = {
      a.map(Success(_)).recover(cause => pureF(Failure(cause)))
    }
  }

  def pureF[A](a: A)(implicit c: Context[F]): F[A] = c.pure(a)
}
