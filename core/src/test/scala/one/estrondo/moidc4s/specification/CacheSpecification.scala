package one.estrondo.moidc4s.specification

import one.estrondo.moidc4s.Cache
import one.estrondo.moidc4s.Context
import one.estrondo.moidc4s.Lookup
import one.estrondo.moidc4s.MockedTestContext
import one.estrondo.moidc4s.Ref
import one.estrondo.moidc4s.TestUnit
import one.estrondo.moidc4s.TestUnitOps
import one.estrondo.moidc4s.syntax._

//noinspection ConvertExpressionToSAM
class CacheSpecification[F[_]: Context: Ref.Maker] extends TestUnitOps {

  def u01: TestUnit[F] =
    mockedTestUnit("It should lookup just once when it is not defined.")(new UnitTestContext[String] {

      def apply() = {
        (lookup
          .apply()(_: Context[F]))
          .expects(*)
          .returning(Context[F].pure("Woohoo!"))
          .once()

        for {
          cache <- cacheF
          v1    <- cache.get
          v2    <- cache.get
        } yield {
          (v1, v2) should be("Woohoo!", "Woohoo!")
        }
      }
    })

  def u02 = mockedTestUnit("It should  report any error in looking up.")(new UnitTestContext[String] {
    def apply() = {
      (lookup
        .apply()(_: Context[F]))
        .expects(*)
        .returning(Context[F].failed(new IllegalStateException("@@@")))
        .once()

      for {
        cache <- cacheF
        v     <- cache.get.toTry
      } yield {
        v.failure.exception shouldBe an[IllegalStateException]
      }
    }
  })

  def u03 = mockedTestUnit("It should invalidate when required.")(new UnitTestContext[String] {
    def apply() = {
      (lookup
        .apply()(_: Context[F]))
        .expects(*)
        .returning(Context[F].pure("Ok!"))
        .once()

      (lookup
        .invalidate()(_: Context[F]))
        .expects(*)
        .returning(Context[F].done)
        .once()

      (lookup
        .apply()(_: Context[F]))
        .expects(*)
        .returning(Context[F].pure("Good!"))
        .once()

      for {
        cache <- cacheF
        v1    <- cache.get
        _     <- cache.invalidate()
        v2    <- cache.get
      } yield {
        (v1, v2) should be("Ok!", "Good!")
      }
    }
  })

  abstract class UnitTestContext[A] extends MockedTestContext[F] {
    val lookup: Lookup[F, A]   = mock[Lookup[F, A]]
    val cacheF: F[Cache[F, A]] = Cache[F, A](lookup)
  }
}
