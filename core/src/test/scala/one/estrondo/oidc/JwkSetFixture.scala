package one.estrondo.oidc

object JwkSetFixture {

  def createRandom(): JwkSet = {
    val (_, _, j1) = ECFixture.createRandom(JwaAlgorithm.Es256)
    val (_, _, j2) = ECFixture.createRandom(JwaAlgorithm.Es384)
    val (_, _, j3) = ECFixture.createRandom(JwaAlgorithm.Es512)
    val (_, _, j4) = RSAFixture.createRandom(JwaAlgorithm.Rs256)
    val (_, _, j5) = RSAFixture.createRandom(JwaAlgorithm.Rs384)
    val (_, _, j6) = RSAFixture.createRandom(JwaAlgorithm.Rs512)
    val (_, j7)    = HMacFixture.createRandom(JwaAlgorithm.Hs256)
    val (_, j8)    = HMacFixture.createRandom(JwaAlgorithm.Hs384)
    val (_, j9)    = HMacFixture.createRandom(JwaAlgorithm.Hs512)
    JwkSet(
      keys = Seq(
        j1,
        j2,
        j3,
        j4,
        j5,
        j6,
        j7,
        j8,
        j9,
      ),
    )
  }
}
