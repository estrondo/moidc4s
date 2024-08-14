package one.estrondo.oidc

import java.util.UUID

object JwtHeaderFixture {

  def createRandom(): JwtHeader = JwtHeader(
    alg = Some(Fixtures.pickOne(JwaAlg.all.map(_.value).toSeq: _*)),
    kid = Some(UUID.randomUUID().toString),
  )
}
