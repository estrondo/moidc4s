package one.estrondo.moidc4s.specification

import one.estrondo.moidc4s.Context
import one.estrondo.moidc4s.Fixtures
import one.estrondo.moidc4s.JwaAlgorithm
import one.estrondo.moidc4s.JwtHeader
import one.estrondo.moidc4s.KeyDescription
import one.estrondo.moidc4s.KeyDescriptionFixture
import one.estrondo.moidc4s.KeyFinder
import one.estrondo.moidc4s.KeySet
import one.estrondo.moidc4s.TestUnitContext
import one.estrondo.moidc4s.TestUnitOps
import one.estrondo.moidc4s.syntax._
import org.scalatest.Assertion
import scala.collection.immutable.HashMap

class KeyFinderSpecification[F[_]: Context] extends TestUnitOps {

  val u01 = testUnit[F]("It should find the key by id and check the algorithm.")(new U {
    override def apply(): F[Assertion] = {
      val expected  = Fixtures.pickOne(keys.values.toSeq: _*)
      val jwtHeader = JwtHeader(alg = expected.alg.map(_.name), kid = expected.kid)

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
      val jwtHeader = JwtHeader(kid = None, alg = expected.alg.map(_.name))

      for (result <- KeyFinder(jwtHeader, keySet)) yield {
        result.value should be(expected)
      }
    }
  })

  val u05 = testUnit[F]("It should use the default key (in this case it is a single key set).")(new U {

    override lazy val keys: HashMap[String, KeyDescription] = {
      val keyDescription = KeyDescriptionFixture.createRandom(JwaAlgorithm.Es512)
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
  abstract class U extends TestUnitContext[F] {
    lazy val keys = HashMap((for (jwaAlg <- JwaAlgorithm.all.toSeq) yield {
      val keyDescription = KeyDescriptionFixture.createRandom(jwaAlg)
      keyDescription.kid.get -> keyDescription
    }): _*)

    lazy val keySet = KeySet(byKid = keys, withoutKid = Nil)
  }
}
