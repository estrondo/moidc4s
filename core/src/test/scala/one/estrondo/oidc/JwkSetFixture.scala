package one.estrondo.oidc

object JwkSetFixture {

  def createRandom(): JwkSet = {
    val (_, _, j1) = ECFixture.createRandomP256()
    val (_, _, j2) = ECFixture.createRandomP384()
    val (_, _, j3) = ECFixture.createRandomP512()
    val (_, _, j4) = RSAFixture.createRandom(JwaAlg.Rs256)
    val (_, _, j5) = RSAFixture.createRandom(JwaAlg.Rs384)
    val (_, _, j6) = RSAFixture.createRandom(JwaAlg.Rs512)
    val (_, j7)    = HMacFixture.createRandomHs256()
    val (_, j8)    = HMacFixture.createRandomHs384()
    val (_, j9)    = HMacFixture.createRandomHs512()
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
