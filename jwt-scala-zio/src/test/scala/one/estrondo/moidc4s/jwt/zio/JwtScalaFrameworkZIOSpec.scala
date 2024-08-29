package one.estrondo.moidc4s.jwt.zio

import one.estrondo.moidc4s.jwt.specification.JwtScalaFrameworkSpecification
import one.estrondo.moidc4s.zio._
import one.estrondo.moidc4s.zio.ZioOidcSpec
import zio.Scope
import zio.json.ast.Json
import zio.test.Spec
import zio.test.TestEnvironment

object JwtScalaFrameworkZIOSpec extends ZioOidcSpec {

  val specification = new JwtScalaFrameworkSpecification[OZIO, Json]

  override def spec: Spec[TestEnvironment with Scope, Any] = suite("The JwtScalaZIO Implementation")(
    test(specification.u01),
    test(specification.u02),
    test(specification.u03),
    test(specification.u04),
    test(specification.u05),
    test(specification.u06),
    test(specification.u07),
    test(specification.u08),
    test(specification.u09),
    test(specification.u10),
    test(specification.u11),
    test(specification.u12),
    test(specification.u20),
    test(specification.u21),
    test(specification.u22),
    test(specification.u30),
    test(specification.u31),
    test(specification.u32),
  )
}
