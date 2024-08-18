package one.estrondo.oidc

object JwtHeaderFixture {

  def createRandom(): JwtHeader = JwtHeader(
    alg = Some(Fixtures.pickOne(JwaAlgorithm.all.map(_.name).toSeq: _*)),
    kid = Some(Fixtures.randomId()),
  )
}
