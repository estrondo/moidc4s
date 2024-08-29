package one.estrondo.moidc4s.specs

import one.estrondo.moidc4s.T
import one.estrondo.moidc4s.specification.JwkSetSourceSpecification

class JwkSetSourceSpec extends OidcSpec {

  val spec = new JwkSetSourceSpecification[T]
  test(spec.u01, spec.u02)
}
