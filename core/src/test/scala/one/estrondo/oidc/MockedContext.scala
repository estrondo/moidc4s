package one.estrondo.oidc

import one.estrondo.oidc.syntax._
import org.scalamock.MockFactoryBase
import org.scalatest.Assertion

trait MockedContext[F[_]] extends (() => F[Assertion]) with MockFactoryBase with ScalatestSpec[F] {

  override type ExpectationException = Exception

  def verified(implicit ctx: Context[F]): F[Assertion] = {
    for (assertion <- apply()) yield {
      withExpectations(())
      assertion
    }
  }

  override protected def newExpectationException(message: String, methodName: Option[Symbol]): ExpectationException = {
    new Exception(s"$message: $methodName")
  }
}
