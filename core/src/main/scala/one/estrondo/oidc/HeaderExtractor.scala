package one.estrondo.oidc
import one.estrondo.oidc.syntax._

private[oidc] object HeaderExtractor {

  def apply[F[_]: Context: JsonFramework](token: String): F[JwtHeader] = {
    token.indexOf('.') match {
      case index if index > 0 =>
        Context[F]
          .attempt(B64.decodeUrlEncodedAsString(token.substring(0, index)))
          .mapError(new OidcException.InvalidToken("Unable to decode JOSE Header's Base64.", _))
          .flatMap(JsonFramework[F].jwtHeader)
          .mapError(new OidcException.InvalidJwt("Unable to parse JOSE Header.", _))
      case _                  =>
        Context[F].failed(new OidcException.InvalidToken("There is no '.' character."))
    }
  }
}
