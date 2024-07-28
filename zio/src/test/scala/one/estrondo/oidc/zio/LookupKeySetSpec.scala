package one.estrondo.oidc.zio

import one.estrondo.oidc.specification.LookupKeySetSpecification

object LookupKeySetSpec extends ZioOidcSpec {

  val specs = new LookupKeySetSpecification[OZIO]

  def spec = suite("LookupKeySetSpec")(
    test(specs.u01),
    test(specs.u02),
  )
}
