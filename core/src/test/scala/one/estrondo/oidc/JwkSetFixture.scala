package one.estrondo.oidc

object JwkSetFixture {

  def createRandom(): JwkSet = {
    val (_, _, j1) = ECFixture.createRandomP256(Some(JwaAlg.Es256))
    val (_, _, j2) = ECFixture.createRandomP384(Some(JwaAlg.Es384))
    val (_, _, j3) = ECFixture.createRandomP521(Some(JwaAlg.Es512))
    JwkSet(
      keys = Seq(
        j1,
        j2,
        j3,
      ),
    )
  }
}
