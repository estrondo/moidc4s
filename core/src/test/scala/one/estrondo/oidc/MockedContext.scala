package one.estrondo.oidc

import one.estrondo.oidc.syntax._
import org.scalamock.MockFactoryBase
import org.scalamock.matchers.Matchers
import org.scalatest.Assertion
import org.scalatest.TryValues

trait MockedContext[F[_]] extends MockFactoryBase with Matchers with TryValues with Specs[F] {

  override type ExpectationException = Exception

  def verified(implicit c: Context[F]): F[Assertion] = {
    for (assertion <- run) yield {
      withExpectations(())
      assertion
    }
  }

  def run: F[Assertion]

  override protected def newExpectationException(message: String, methodName: Option[Symbol]): ExpectationException = {
    new Exception(s"$message: $methodName")
  }
}
