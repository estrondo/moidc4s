package one.estrondo.oidc.zio

import one.estrondo.oidc.specification.LookupJwkSetSpecification

object LookupJwkSetSpec extends ZioOidcSpec {

  val specs = new LookupJwkSetSpecification

  def spec = suite("LookJwkSetSpec")(
    test(specs.u01),
    test(specs.u02)
  )
}
