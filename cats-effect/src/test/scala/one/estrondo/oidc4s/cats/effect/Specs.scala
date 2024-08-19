package one.estrondo.oidc4s.cats.effect

import cats.effect.IO
import one.estrondo.oidc.specification.CacheSpecification
import one.estrondo.oidc.specification.HeaderExtractorSpecification
import one.estrondo.oidc.specification.JwkSetSourceSpecification
import one.estrondo.oidc.specification.KeyFinderSpecification

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
