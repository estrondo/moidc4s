package one.estrondo.moidc4s.zio.json

import one.estrondo.moidc4s.OidcException

class ZIOJsonException(message: String, cause: Throwable = null) extends OidcException(message, cause)
