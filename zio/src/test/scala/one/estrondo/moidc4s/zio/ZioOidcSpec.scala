package one.estrondo.moidc4s.zio

import one.estrondo.moidc4s.TestUnit
import org.scalatest.Succeeded
import zio.test.Spec
import zio.test.ZIOSpecDefault
import zio.test.assertTrue

abstract class ZioOidcSpec extends ZIOSpecDefault {

  protected def test(unit: TestUnit[OZIO]): Spec[Any, Throwable] =
    test(unit.name) {
      for (assertion <- unit.unit) yield {
        assertTrue(assertion.isInstanceOf[Succeeded.type])
      }
    }

}
