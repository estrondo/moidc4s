package one.estrondo.oidc4s.specs

import one.estrondo.oidc4s.T
import one.estrondo.oidc4s.TestUnit
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
