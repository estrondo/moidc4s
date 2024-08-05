package one.estrondo.oidc.specification

import one.estrondo.oidc.Cache
import one.estrondo.oidc.Context
import one.estrondo.oidc.Lookup
import one.estrondo.oidc.MockedContext
import one.estrondo.oidc.Ref
import one.estrondo.oidc.TestUnit
import one.estrondo.oidc.TestUnitOps
import one.estrondo.oidc.syntax._

//noinspection ConvertExpressionToSAM
class CacheSpecification[F[_]: Context: Ref.Maker] extends TestUnitOps {

  def u01: TestUnit[F] = mockedTestUnit("It should lookup just once when it is not defined.")(new UnitContext[String] {

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

  def u02 = mockedTestUnit("It should  report any error in looking up.")(new UnitContext[String] {
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

  def u03 = mockedTestUnit("It should invalidate when required.")(new UnitContext[String] {
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

  abstract class UnitContext[A] extends MockedContext[F] {
    val lookup: Lookup[F, A]   = mock[Lookup[F, A]]
    val cacheF: F[Cache[F, A]] = Cache[F, A](lookup)
  }
}
