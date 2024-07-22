package one.estrondo.oidc.specs

import one.estrondo.oidc.T
import one.estrondo.oidc.specification.LookupMetadataSpecification

class LookupMetadataSpec extends OidcSpec {

  private val specs = new LookupMetadataSpecification[T]
  test(specs.u01, specs.u02, specs.u03, specs.u04)

}
