package one.estrondo.oidc

import java.math.BigInteger
import java.util.Base64

trait Base64Ops {

  val base64UrlEncoder: Base64.Encoder = Base64.getUrlEncoder

  def encodeBase64UrlEncoded(x: BigInteger): String =
    base64UrlEncoder.encodeToString(x.toByteArray)
}
