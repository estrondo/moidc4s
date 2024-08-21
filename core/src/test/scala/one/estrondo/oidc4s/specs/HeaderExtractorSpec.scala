package one.estrondo.oidc4s.specs

import one.estrondo.oidc4s.T
import one.estrondo.oidc4s.specification.HeaderExtractorSpecification

class HeaderExtractorSpec extends OidcSpec {

  val specification = new HeaderExtractorSpecification[T]
  test(specification.u01)
}
