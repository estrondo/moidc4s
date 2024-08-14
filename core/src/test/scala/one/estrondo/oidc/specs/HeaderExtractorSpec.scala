package one.estrondo.oidc.specs

import one.estrondo.oidc.T
import one.estrondo.oidc.specification.HeaderExtractorSpecification

class HeaderExtractorSpec extends OidcSpec {

  val specification = new HeaderExtractorSpecification[T]
  test(specification.u01)
}
