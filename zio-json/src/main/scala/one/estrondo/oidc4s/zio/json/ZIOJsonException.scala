package one.estrondo.oidc4s.zio.json

import one.estrondo.oidc.OidcException

class ZIOJsonException(message: String, cause: Throwable = null) extends OidcException(message, cause)
