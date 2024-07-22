package one.estrondo.oidc.zio
import one.estrondo.oidc.specification.LookupMetadataSpecification

object LookupMetadataSpec extends ZioOidcSpec {

  val specs = new LookupMetadataSpecification[OZIO]

  def spec = suite("LookupMetadata")(
    test(specs.u01),
    test(specs.u02),
    test(specs.u03),
    test(specs.u04),
  )
}
