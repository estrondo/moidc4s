package one.estrondo.oidc

sealed abstract class OidcException(message: String = null, cause: Throwable = null)
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
}
