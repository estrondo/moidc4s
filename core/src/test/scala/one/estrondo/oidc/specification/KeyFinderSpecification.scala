package one.estrondo.oidc.specification

import one.estrondo.oidc.Context
import one.estrondo.oidc.Fixtures
import one.estrondo.oidc.JwaAlg
import one.estrondo.oidc.JwtHeader
import one.estrondo.oidc.KeyDescription
import one.estrondo.oidc.KeyDescriptionFixture
import one.estrondo.oidc.KeyFinder
import one.estrondo.oidc.KeySet
import one.estrondo.oidc.TestContext
import one.estrondo.oidc.TestUnitOps
import one.estrondo.oidc.syntax._
import org.scalatest.Assertion
import scala.collection.immutable.HashMap

class KeyFinderSpecification[F[_]: Context] extends TestUnitOps {

  val u01 = testUnit[F]("It should find the key by id and check the algorithm.")(new U {
    override def apply(): F[Assertion] = {
      val expected  = Fixtures.pickOne(keys.values.toSeq: _*)
      val jwtHeader = JwtHeader(alg = expected.alg.map(_.value), kid = expected.kid)

      for (result <- KeyFinder(jwtHeader, keySet)) yield {
        result.value should be(expected)
      }
    }
  })

  val u02 = testUnit[F]("It should find the key by id, but reject due to algorithm mismatch.")(new U {
    override def apply(): F[Assertion] = {
      val unexpected = Fixtures.pickOne(keys.values.toSeq: _*)
      val jwtHeader  = JwtHeader(alg = Some("OTHER!"), kid = unexpected.kid)

      for (result <- KeyFinder(jwtHeader, keySet)) yield {
        result should be(empty)
      }
    }
  })

  val u03 = testUnit[F]("It should find the key just by id.")(new U {
    override def apply(): F[Assertion] = {
      val expected  = Fixtures.pickOne(keys.values.toSeq: _*)
      val jwtHeader = JwtHeader(kid = expected.kid, alg = None)

      for (result <- KeyFinder(jwtHeader, keySet)) yield {
        result.value should be(expected)
      }
    }
  })

  val u04 = testUnit[F]("It should find the key by algorithm.")(new U {
    override def apply(): F[Assertion] = {
      val expected  = Fixtures.pickOne(keys.values.toSeq: _*)
      val jwtHeader = JwtHeader(kid = None, alg = expected.alg.map(_.value))

      for (result <- KeyFinder(jwtHeader, keySet)) yield {
        result.value should be(expected)
      }
    }
  })

  val u05 = testUnit[F]("It should use the default key (in this case it is a single key set).")(new U {

    override lazy val keys: HashMap[String, KeyDescription] = {
      val keyDescription = KeyDescriptionFixture.createRandom(JwaAlg.Es512)
      HashMap(keyDescription.kid.get -> keyDescription)
    }

    override def apply(): F[Assertion] = {
      val expected  = keys.head._2
      val jwtHeader = JwtHeader(kid = None, alg = None)

      for (result <- KeyFinder(jwtHeader, keySet)) yield {
        result.value should be(expected)
      }
    }
  })

  // noinspection TypeAnnotation
  abstract class U extends TestContext[F] {
    lazy val keys = HashMap.from(for (jwaAlg <- JwaAlg.all) yield {
      val keyDescription = KeyDescriptionFixture.createRandom(jwaAlg)
      keyDescription.kid.get -> keyDescription
    })

    lazy val keySet = KeySet(byKid = keys, withoutKid = Nil)
  }
}
