package one.estrondo.oidc

import org.scalatest.Assertion

trait TestContext[F[_]] extends (() => F[Assertion]) with ScalatestSpec[F]
