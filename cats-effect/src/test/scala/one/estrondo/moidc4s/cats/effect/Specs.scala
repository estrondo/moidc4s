package one.estrondo.moidc4s.cats.effect

import cats.effect.IO
import one.estrondo.moidc4s.specification.CacheSpecification
import one.estrondo.moidc4s.specification.HeaderExtractorSpecification
import one.estrondo.moidc4s.specification.JwkSetSourceSpecification
import one.estrondo.moidc4s.specification.KeyFinderSpecification

class Specs extends Oidc4sSpec {

  val cacheSpecs           = new CacheSpecification[IO]
  val headerExtractorSpecs = new HeaderExtractorSpecification[IO]
  val jwkSetSourceSpecs    = new JwkSetSourceSpecification[IO]
  val keyFinderSpecs       = new KeyFinderSpecification[IO]

  test(cacheSpecs.u01, cacheSpecs.u02, cacheSpecs.u03)
  test(headerExtractorSpecs.u01)
  test(jwkSetSourceSpecs.u01, jwkSetSourceSpecs.u02)
  test(
    keyFinderSpecs.u01,
    keyFinderSpecs.u02,
    keyFinderSpecs.u03,
    keyFinderSpecs.u04,
    keyFinderSpecs.u05,
  )

}
