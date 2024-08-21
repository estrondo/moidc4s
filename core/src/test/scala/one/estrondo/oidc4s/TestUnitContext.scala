package one.estrondo.oidc4s

import org.scalatest.Assertion

trait TestUnitContext[F[_]] extends (() => F[Assertion]) with ScalatestTestContext[F]
