package one.estrondo.moidc4s.specs

import one.estrondo.moidc4s.T
import one.estrondo.moidc4s.specification.HeaderExtractorSpecification

class HeaderExtractorSpec extends OidcSpec {

  val specification = new HeaderExtractorSpecification[T]
  test(specification.u01)
}
