package one.estrondo.oidc

import java.math.BigInteger
import java.util.Base64

private[oidc] object B64 {

  lazy val UrlDecoder: Base64.Decoder = Base64.getUrlDecoder

  def decodeUrlEncoded(name: String, parameter: Option[String]): BigInteger = {
    parameter match {
      case Some(content) =>
        try {
          new BigInteger(UrlDecoder.decode(content))
        } catch {
          case cause: Throwable =>
            throw new OidcException.InvalidParameter(s"Unable to decode Base64UrlEncoded parameter '$name'.", cause)
        }
      case None          =>
        throw new OidcException.InvalidParameter(s"The parameter '$name' is undefined.")
    }
  }

}
