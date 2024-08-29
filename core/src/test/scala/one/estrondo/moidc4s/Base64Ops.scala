package one.estrondo.moidc4s

import java.math.BigInteger
import java.nio.charset.StandardCharsets
import java.util.Base64

object Base64Ops extends Base64Ops

trait Base64Ops {

  val base64UrlEncoder: Base64.Encoder = Base64.getUrlEncoder

  def encodeBase64UrlEncoded(x: BigInteger): String =
    encodeBase64UrlEncoded(x.toByteArray)

  def encodeBase64UrlEncoded(x: String): String =
    encodeBase64UrlEncoded(x.getBytes(StandardCharsets.UTF_8))

  def encodeBase64UrlEncoded(x: Array[Byte]): String =
    base64UrlEncoder.encodeToString(x)
}
