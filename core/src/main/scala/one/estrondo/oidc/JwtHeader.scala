package one.estrondo.oidc

case class JwtHeader(
    alg: Option[String],
    kid: Option[String],
)
