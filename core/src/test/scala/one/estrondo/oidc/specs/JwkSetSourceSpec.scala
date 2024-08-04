package one.estrondo.oidc.specs

import one.estrondo.oidc.T
import one.estrondo.oidc.specification.JwkSetSourceSpecification

class JwkSetSourceSpec extends OidcSpec {

  val spec = new JwkSetSourceSpecification[T]
  test(spec.u01, spec.u02)
}
