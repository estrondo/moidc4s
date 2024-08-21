package one.estrondo.oidc4s.specs

import one.estrondo.oidc4s.T
import one.estrondo.oidc4s.specification.JwkSetSourceSpecification

class JwkSetSourceSpec extends OidcSpec {

  val spec = new JwkSetSourceSpecification[T]
  test(spec.u01, spec.u02)
}
