package one.estrondo.moidc4s

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

case class JwkSet(
    keys: Seq[Jwk],
)
