package one.estrondo.oidc.specification

import one.estrondo.oidc.Context
import one.estrondo.oidc.JwkSet
import one.estrondo.oidc.JwkSetFixture
import one.estrondo.oidc.Lookup
import one.estrondo.oidc.LookupKeySet
import one.estrondo.oidc.MockedContext
import one.estrondo.oidc.OidcException
import one.estrondo.oidc.TestUnitOps
import one.estrondo.oidc.specs.OidcSpec.TryF
import one.estrondo.oidc.syntax._
import org.scalatest.TryValues
import org.scalatest.matchers.should.Matchers

class LookupKeySetSpecification[F[_]: Context] extends TryValues with TestUnitOps with Matchers {

  val u01 = testUnit("It should parse correctly a JwkSet.")(
    new JwkSetTestContext {

      def run = {

        val expectedJwkSet = JwkSetFixture.createRandom()

        (underling.apply _)
          .expects()
          .returning(Context[F].pure(expectedJwkSet))

        for (result <- lookup()) yield {
          result.withoutKid shouldBe empty
          result.byKid.keySet should contain theSameElementsAs expectedJwkSet.keys.map(_.kid.get)
        }
      }

    }.verified,
  )

  val u02 = testUnit("It should report any unexpected error.")(new JwkSetTestContext {
    def run = {
      (underling.apply _)
        .expects()
        .returning(Context[F].failed(new IllegalStateException("###")))
        .once()

      for (result <- lookup().toTry) yield {
        result.failure.exception shouldBe an[OidcException.Unexpected]
      }
    }
  }.verified)

  abstract private class JwkSetTestContext extends MockedContext[F] {

    val underling: Lookup[F, JwkSet] = mock[Lookup[F, JwkSet]]

    val lookup: LookupKeySet.FromJwkSet[F] = new LookupKeySet.FromJwkSet[F](underling)
  }
}
