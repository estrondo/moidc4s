package one.estrondo.oidc

import org.scalatest.Assertion

trait TestUnitContext[F[_]] extends (() => F[Assertion]) with ScalatestTestContext[F]
