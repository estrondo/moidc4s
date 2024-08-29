package one.estrondo.moidc4s.specs

import one.estrondo.moidc4s.T
import one.estrondo.moidc4s.TestUnit
import org.scalatest.freespec.AnyFreeSpec

abstract class OidcSpec extends AnyFreeSpec {

  protected def test(testUnits: TestUnit[T]*): Unit = {
    for (testUnit <- testUnits) {
      testUnit.name in {
        testUnit.unit.run(identity).get
      }
    }
  }
}
