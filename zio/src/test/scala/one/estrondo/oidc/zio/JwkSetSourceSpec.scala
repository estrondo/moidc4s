package one.estrondo.oidc.zio

import one.estrondo.oidc.specification.JwkSetSourceSpecification

object JwkSetSourceSpec extends ZioOidcSpec {

  val specs = new JwkSetSourceSpecification[OZIO]

  def spec = suite("JwkSetSource")(
    test(specs.u01),
    test(specs.u02),
  )
}
