package one.estrondo.oidc.specs

import one.estrondo.oidc.T
import one.estrondo.oidc.specification.KeyFinderSpecification

class KeyFinderSpec extends OidcSpec {

  val specs = new KeyFinderSpecification[T]
  test(specs.u01, specs.u02, specs.u03, specs.u04)
}
