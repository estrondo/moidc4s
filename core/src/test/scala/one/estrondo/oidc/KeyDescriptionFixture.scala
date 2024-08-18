package one.estrondo.oidc

import java.security.PublicKey
import javax.crypto.SecretKey

object KeyDescriptionFixture {

  def createRandom(algorithm: JwaAlgorithm): KeyDescription = {
    algorithm match {
      case algorithm: JwaAlgorithm.Hmac => create(algorithm, HMacFixture.createRandom(algorithm)._1)
      case algorithm: JwaAlgorithm.Ec   => create(algorithm, ECFixture.createRandom(algorithm)._1)
      case algorithm: JwaAlgorithm.Rsa  => create(algorithm, RSAFixture.createRandom(algorithm)._1)
      case _                            => ???
    }
  }

  def create(algorithm: JwaAlgorithm, key: PublicKey): KeyDescription = {
    create(algorithm, KeyDescription.Public(key))
  }

  def create(algorithm: JwaAlgorithm, key: SecretKey): KeyDescription = {
    create(algorithm, KeyDescription.Secret(key))
  }

  def create(algorithm: JwaAlgorithm, key: KeyDescription.Key): KeyDescription = {
    KeyDescription(
      kid = Some(Fixtures.randomId()),
      alg = Some(algorithm),
      key = key,
    )
  }
}
