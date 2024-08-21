package one.estrondo.oidc4s

case class JwtHeader(
    alg: Option[String],
    kid: Option[String],
)
