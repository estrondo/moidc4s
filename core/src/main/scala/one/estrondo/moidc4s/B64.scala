package one.estrondo.moidc4s

import java.math.BigInteger
import java.nio.charset.StandardCharsets
import java.util.Base64

private[moidc4s] object B64 {

  lazy val UrlDecoder: Base64.Decoder = Base64.getUrlDecoder

  def decodeUrlEncodedAsString(encoded: String): String = {
    new String(UrlDecoder.decode(encoded), StandardCharsets.UTF_8)
  }

  def decodeUrlEncodedAsBigInteger(name: String, parameter: Option[String]): BigInteger = {
    new BigInteger(decodeUrlEncoded(name, parameter))
  }

  def decodeUrlEncoded(name: String, parameter: Option[String]): Array[Byte] = {
    parameter match {
      case Some(content) =>
        try {
          UrlDecoder.decode(content)
        } catch {
          case cause: Throwable =>
            throw new OidcException.InvalidParameter(s"Unable to decode Base64UrlEncoded parameter '$name'.", cause)
        }
      case None          =>
        throw new OidcException.InvalidParameter(s"The parameter '$name' is undefined.")
    }
  }

}
