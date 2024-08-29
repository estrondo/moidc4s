package one.estrondo.moidc4s.jwt

import _root_.zio.json.ast.Json
import java.security.PublicKey
import javax.crypto.SecretKey
import pdi.jwt.JwtOptions
import pdi.jwt.JwtZIOJson
import pdi.jwt.algorithms.JwtAsymmetricAlgorithm
import pdi.jwt.algorithms.JwtHmacAlgorithm
import scala.util.Try

package object zio {

  implicit object JwtScalaZIOJsonLibrary extends JwtScalaJsonLibrary[Json] {

    override def decode(token: String, options: JwtOptions): Try[Json] =
      JwtZIOJson.decodeJson(token, options)

    override def decode(
        token: String,
        key: PublicKey,
        algorithm: JwtAsymmetricAlgorithm,
        options: JwtOptions,
    ): Try[Json] = JwtZIOJson.decodeJson(token, key, Seq(algorithm), options)

    override def decode(token: String, key: SecretKey, algorithm: JwtHmacAlgorithm, options: JwtOptions): Try[Json] =
      JwtZIOJson.decodeJson(token, key, Seq(algorithm), options)

    override def decode(token: String, key: PublicKey, options: JwtOptions): Try[Json] =
      JwtZIOJson.decodeJson(token, key, options)

    override def decode(token: String, key: SecretKey, options: JwtOptions): Try[Json] =
      JwtZIOJson.decodeJson(token, key, options)
  }
}
