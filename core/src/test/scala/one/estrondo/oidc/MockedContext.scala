package one.estrondo.oidc

import org.scalamock.MockFactoryBase
import org.scalamock.matchers.Matchers
import org.scalatest.Assertion
import syntax._

trait MockedContext[F[_]] extends MockFactoryBase with Matchers with Specs[F] {

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
