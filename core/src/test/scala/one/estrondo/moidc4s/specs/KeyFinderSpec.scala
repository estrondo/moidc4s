package one.estrondo.moidc4s.specs

import one.estrondo.moidc4s.T
import one.estrondo.moidc4s.specification.KeyFinderSpecification

class KeyFinderSpec extends OidcSpec {

  val specs = new KeyFinderSpecification[T]
  test(specs.u01, specs.u02, specs.u03, specs.u04)
}
