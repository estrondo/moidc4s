package one.estrondo.oidc

import java.security.PublicKey
import javax.crypto.SecretKey

object KeyDescriptionFixture {

  def createRandom(jwaAlg: JwaAlg): KeyDescription = {
    jwaAlg match {
      case JwaAlg.Hs256 => create(jwaAlg, HMacFixture.createRandomHs256()._1)
      case JwaAlg.Hs384 => create(jwaAlg, HMacFixture.createRandomHs384()._1)
      case JwaAlg.Hs512 => create(jwaAlg, HMacFixture.createRandomHs512()._1)
      case JwaAlg.Es256 => create(jwaAlg, ECFixture.createRandomP256()._1)
      case JwaAlg.Es384 => create(jwaAlg, ECFixture.createRandomP384()._1)
      case JwaAlg.Es512 => create(jwaAlg, ECFixture.createRandomP512()._1)
      case JwaAlg.Rs512 => create(jwaAlg, RSAFixture.createRandom(jwaAlg)._1)
      case JwaAlg.Rs384 => create(jwaAlg, RSAFixture.createRandom(jwaAlg)._1)
      case JwaAlg.Rs256 => create(jwaAlg, RSAFixture.createRandom(jwaAlg)._1)
    }
  }

  def create(alg: JwaAlg, key: PublicKey): KeyDescription = {
    create(alg, KeyDescription.Public(key))
  }

  def create(alg: JwaAlg, key: SecretKey): KeyDescription = {
    create(alg, KeyDescription.Secret(key))
  }

  def create(alg: JwaAlg, key: KeyDescription.Key): KeyDescription = {
    KeyDescription(
      kid = Some(Fixtures.randomId()),
      alg = Some(alg),
      key = key,
    )
  }
}
