package one.estrondo.moidc4s

abstract class OidcException(message: String = null, cause: Throwable = null)
    extends RuntimeException(message, cause, false, false)

object OidcException {

  class FailedRequest(
      message: String,
      val status: Int,
      val headers: Map[String, Seq[String]],
      val url: String,
      val body: String,
  ) extends OidcException(message) {

    def this(message: String, url: String, response: Transporter.Failed) = this(
      message,
      status = response.status,
      headers = response.headers,
      url = url,
      body = response.body,
    )
  }

  class Unexpected(message: String, cause: Throwable = null) extends OidcException(message, cause)

  class NoJwkSet(message: String = "", cause: Throwable = null) extends OidcException(message, cause)

  class InvalidJwk(message: String, cause: Throwable = null) extends OidcException(message, cause)

  class InvalidParameter(message: String, cause: Throwable = null) extends OidcException(message, cause)

  class InvalidMetadata(message: String, cause: Throwable = null) extends OidcException(message, cause)

  class InvalidToken(message: String, cause: Throwable = null) extends OidcException(message, cause)

  class InvalidJwt(message: String, cause: Throwable = null) extends OidcException(message, cause)

  class AmbiguousException(message: String, cause: Throwable = null) extends OidcException(message, cause)

  class UnsupportedAlgorithm(message: String, cause: Throwable = null) extends OidcException(message, cause)

  class RSAException(jwk: Jwk, cause: Throwable = null)
      extends OidcException(
        message = s"n=${getOrUndefined(jwk.n)}, e=${getOrUndefined(jwk.e)}",
        cause = cause,
      )

  class ECException(jwk: Jwk, cause: Throwable = null)
      extends OidcException(
        message = s"crv=${getOrUndefined(jwk.crv)}, x=${getOrUndefined(jwk.x)}, y=${getOrUndefined(jwk.y)}",
        cause = cause,
      )

  private def getOrUndefined(option: Option[String]): String =
    option.getOrElse("<undefined>")
}
