package one.estrondo.oidc

import org.scalamock.MockFactoryBase
import org.scalamock.matchers.Matchers
import org.scalatest.Assertion
import syntax._

abstract class MockedContext[F[_]: Context] extends MockFactoryBase with Matchers {

  override type ExpectationException = Exception

  def verified: F[Assertion] = {
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
