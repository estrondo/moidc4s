package one.estrondo.oidc.specs

import one.estrondo.oidc.T
import one.estrondo.oidc.specification.LookupKeySetSpecification

class LookupKeySetSpec extends OidcSpec {

  val specs = new LookupKeySetSpecification[T]
  test(specs.u01, specs.u02)
}
