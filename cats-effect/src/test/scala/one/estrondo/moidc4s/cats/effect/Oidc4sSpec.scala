package one.estrondo.moidc4s.cats.effect

import cats.effect.IO
import cats.effect.testing.scalatest.AsyncIOSpec
import one.estrondo.moidc4s.TestUnit
import org.scalatest.freespec.AsyncFreeSpec

abstract class Oidc4sSpec extends AsyncFreeSpec with AsyncIOSpec {

  def test(units: TestUnit[IO]*) = {
    for (unit <- units) unit.name in unit.unit
  }
}
