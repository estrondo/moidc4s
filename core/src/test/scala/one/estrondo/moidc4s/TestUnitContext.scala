package one.estrondo.moidc4s

import org.scalatest.Assertion

trait TestUnitContext[F[_]] extends (() => F[Assertion]) with ScalatestTestContext[F]
