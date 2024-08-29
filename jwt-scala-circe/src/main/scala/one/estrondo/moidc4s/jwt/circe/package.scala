package one.estrondo.moidc4s.jwt

import io.circe.Json
import java.security.PublicKey
import javax.crypto.SecretKey
import pdi.jwt.JwtCirce
import pdi.jwt.JwtOptions
import pdi.jwt.algorithms.JwtAsymmetricAlgorithm
import pdi.jwt.algorithms.JwtHmacAlgorithm
import scala.util.Try

package object circe {

  implicit object JwtScalaCirceJsonLibrary extends JwtScalaJsonLibrary[Json] {

    override def decode(token: String, options: JwtOptions): Try[Json] =
      JwtCirce.decodeJson(token, options)

    override def decode(
        token: String,
        key: PublicKey,
        algorithm: JwtAsymmetricAlgorithm,
        options: JwtOptions,
    ): Try[Json] =
      JwtCirce.decodeJson(token, key, options)

    override def decode(token: String, key: SecretKey, algorithm: JwtHmacAlgorithm, options: JwtOptions): Try[Json] =
      JwtCirce.decodeJson(token, key, options)

    override def decode(token: String, key: PublicKey, options: JwtOptions): Try[Json] =
      JwtCirce.decodeJson(token, key, options)

    override def decode(token: String, key: SecretKey, options: JwtOptions): Try[Json] =
      JwtCirce.decodeJson(token, key, options)
  }
}
