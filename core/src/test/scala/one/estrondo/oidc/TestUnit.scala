package one.estrondo.oidc

import org.scalatest.Assertion

case class TestUnit[F[_]](name: String, unit: F[Assertion])
