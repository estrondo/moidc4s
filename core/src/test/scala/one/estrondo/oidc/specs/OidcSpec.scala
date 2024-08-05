package one.estrondo.oidc.specs

import one.estrondo.oidc.T
import one.estrondo.oidc.TestUnit
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

abstract class OidcSpec extends AnyFreeSpec with Matchers {

  protected def test(testUnits: TestUnit[T]*): Unit = {
    for (testUnit <- testUnits) {
      testUnit.name in {
        testUnit.unit.run(identity).get
      }
    }
  }
}
