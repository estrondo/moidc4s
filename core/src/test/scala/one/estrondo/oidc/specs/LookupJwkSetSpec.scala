package one.estrondo.oidc.specs

import one.estrondo.oidc.T
import one.estrondo.oidc.specification.LookupJwkSetSpecification

class LookupJwkSetSpec extends OidcSpec {

  val spec = new LookupJwkSetSpecification[T]
  test(spec.u01, spec.u02)
}
