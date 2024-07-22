package one.estrondo.oidc.specs

import one.estrondo.oidc.T
import one.estrondo.oidc.specification.CacheSpecification

class CacheSpec extends OidcSpec {

  private val specs = new CacheSpecification[T]
  test(specs.u01, specs.u02, specs.u03)

}
