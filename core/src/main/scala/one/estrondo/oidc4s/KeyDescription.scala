package one.estrondo.oidc4s

import java.security.PublicKey
import javax.crypto.SecretKey

case class KeyDescription(
    kid: Option[String],
    alg: Option[JwaAlgorithm],
    key: KeyDescription.Key,
)

object KeyDescription {
  sealed trait Key
  case class Public(key: PublicKey) extends Key
  case class Secret(key: SecretKey) extends Key
}
