package one.estrondo.oidc4s.specs

import one.estrondo.oidc4s.T
import one.estrondo.oidc4s.specification.CacheSpecification

class CacheSpec extends OidcSpec {

  private val specs = new CacheSpecification[T]
  test(specs.u01, specs.u02, specs.u03)

}
