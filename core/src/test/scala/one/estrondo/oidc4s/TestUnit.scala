package one.estrondo.oidc4s

import org.scalatest.Assertion

trait TestUnitOps {

  protected def mockedTestUnit[F[_]: Context](name: String)(block: MockedTestContext[F]): TestUnit[F] = {
    val fullName = s"${getClass.getSimpleName.replaceAll("Specification$", "")}: $name"
    TestUnit[F](fullName, block.verified)
  }

  protected def testUnit[F[_]](name: String)(block: TestUnitContext[F]): TestUnit[F] = {
    val fullName = s"${getClass.getSimpleName.replaceAll("Specification$", "")}: $name"
    TestUnit[F](fullName, block());
  }
}

case class TestUnit[F[_]](name: String, unit: F[Assertion])
