package one.estrondo.oidc

import org.scalatest.Assertion

trait TestUnitOps {

  protected def testUnit[F[_]](name: String)(unit: F[Assertion]): TestUnit[F] = {
    TestUnit(
      name = s"${getClass.getSimpleName.replaceAll("Specification$", "")}: $name",
      unit = unit,
    )
  }
}

case class TestUnit[F[_]](name: String, unit: F[Assertion])
