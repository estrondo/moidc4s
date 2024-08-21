package one.estrondo.oidc4s

trait JsonFramework[F[_]] {

  def metadata(body: String): F[Metadata]

  def jwkSet(body: String): F[JwkSet]

  def jwtHeader(body: String): F[JwtHeader]
}

object JsonFramework {

  @inline def apply[F[_]: JsonFramework]: JsonFramework[F] = implicitly[JsonFramework[F]]
}
