package one.estrondo.moidc4s.jwt

import java.security.PublicKey
import javax.crypto.SecretKey
import pdi.jwt.JwtOptions
import pdi.jwt.algorithms.JwtAsymmetricAlgorithm
import pdi.jwt.algorithms.JwtHmacAlgorithm
import scala.util.Try

object JwtScalaJsonLibrary {

  @inline def apply[J: JwtScalaJsonLibrary]: JwtScalaJsonLibrary[J] = implicitly[JwtScalaJsonLibrary[J]]
}

trait JwtScalaJsonLibrary[J] {

  def decode(token: String, options: JwtOptions): Try[J]

  def decode(token: String, key: PublicKey, algorithm: JwtAsymmetricAlgorithm, options: JwtOptions): Try[J]

  def decode(token: String, key: SecretKey, algorithm: JwtHmacAlgorithm, options: JwtOptions): Try[J]

  def decode(token: String, key: PublicKey, options: JwtOptions): Try[J]

  def decode(token: String, key: SecretKey, options: JwtOptions): Try[J]
}
