package one.estrondo.oidc4s.zio

import one.estrondo.oidc4s.specification.CacheSpecification

object CacheSpec extends ZioOidcSpec {

  val specs = new CacheSpecification[OZIO]

  override def spec = suite("CacheSpec")(
    test(specs.u01),
    test(specs.u02),
    test(specs.u03),
  )
}
