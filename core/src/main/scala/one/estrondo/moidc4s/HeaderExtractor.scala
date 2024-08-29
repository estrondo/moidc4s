package one.estrondo.moidc4s
import one.estrondo.moidc4s.syntax._

private[moidc4s] object HeaderExtractor {

  def apply[F[_]: Context: JsonFramework](token: String): F[JwtHeader] = {
    token.indexOf('.') match {
      case index if index > 0 =>
        Context[F]
          .attempt(B64.decodeUrlEncodedAsString(token.substring(0, index)))
          .mapError(new OidcException.InvalidToken("Unable to decode JOSE Header's Base64.", _))
          .flatMap(JsonFramework[F].jwtHeader)
          .mapError(new OidcException.InvalidJwt("Unable to parse JOSE Header.", _))
      case _                  =>
        Context[F].failed(new OidcException.InvalidToken("Invalid JOSE Header."))
    }
  }
}
