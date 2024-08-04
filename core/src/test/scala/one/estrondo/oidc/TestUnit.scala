package one.estrondo.oidc

import org.scalatest.Assertion

trait TestUnitOps {

  protected def mockedTestUnit[F[_]: Context](name: String)(block: MockedContext[F]): TestUnit[F] = {
    val fullName = s"${getClass.getSimpleName.replaceAll("Specification$", "")}: $name"
    TestUnit[F](fullName, block.verified)
  }
}

case class TestUnit[F[_]](name: String, unit: F[Assertion])
