package one.estrondo.oidc

case class Jwk(
    alg: Option[String] = None,
    kid: Option[String] = None,
    kty: Option[String] = None,

    // EC
    crv: Option[String] = None,
    x: Option[String] = None,
    y: Option[String] = None,

    // RSA
    n: Option[String] = None,
    e: Option[String] = None,

    // Symmetric
    k: Option[String] = None,
    use: Option[String] = None,
)
