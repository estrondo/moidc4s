package one.estrondo.moidc4s.zio

import one.estrondo.moidc4s.specification.JwkSetSourceSpecification

object JwkSetSourceSpec extends ZioOidcSpec {

  val specs = new JwkSetSourceSpecification[OZIO]

  def spec = suite("JwkSetSource")(
    test(specs.u01),
    test(specs.u02),
  )
}
