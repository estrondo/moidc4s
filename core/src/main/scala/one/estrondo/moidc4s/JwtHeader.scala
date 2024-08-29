package one.estrondo.moidc4s

case class JwtHeader(
    alg: Option[String],
    kid: Option[String],
)
