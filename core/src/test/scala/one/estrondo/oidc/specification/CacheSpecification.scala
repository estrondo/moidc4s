package one.estrondo.oidc.specification

import one.estrondo.oidc.Cache
import one.estrondo.oidc.Context
import one.estrondo.oidc.Lookup
import one.estrondo.oidc.MockedContext
import one.estrondo.oidc.RefMaker
import one.estrondo.oidc.TestUnit
import one.estrondo.oidc.TestUnitOps
import one.estrondo.oidc.syntax._
import org.scalatest.EitherValues
import org.scalatest.matchers.should.Matchers

class CacheSpecification[F[_]: Context: RefMaker] extends EitherValues with TestUnitOps {

  def u01: TestUnit[F] = testUnit("It should lookup just once when it is not defined.")(
    unit = new UnitContext[String] {

      def run = {
        (lookup.apply _)
          .expects()
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
    }.verified,
  )

  def u02 = testUnit("It should  report any error in looking up.")(new UnitContext[String] {
    def run = {
      (lookup.apply _)
        .expects()
        .returning(Context[F].failed(new IllegalStateException("@@@")))
        .once()

      for {
        cache <- cacheF
        v     <- cache.get
                   .map(Right(_))
                   .recover(e => Context[F].pure(Left(e): Either[Throwable, String]))
      } yield {
        v.left.value shouldBe an[IllegalStateException]
      }
    }
  }.verified)

  def u03 = testUnit("It should invalidate when required.")(
    unit = new UnitContext[String] {
      def run = {
        (lookup.apply _)
          .expects()
          .returning(Context[F].pure("Ok!"))
          .once()

        (lookup.invalidate _)
          .expects()
          .returning(Context[F].done)
          .once()

        (lookup.apply _)
          .expects()
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
    }.verified,
  )

  abstract class UnitContext[A] extends MockedContext[F] with Matchers {
    val lookup: Lookup[F, A]   = mock[Lookup[F, A]]
    val cacheF: F[Cache[F, A]] = Cache[F, A](lookup)
  }
}
