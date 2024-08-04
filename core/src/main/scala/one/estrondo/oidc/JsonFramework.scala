package one.estrondo.oidc

trait JsonFramework[F[_]] {

  def metadata(body: String): F[Metadata]

  def jwkSet(body: String): F[JwkSet]
}

object JsonFramework {

  @inline def apply[F[_]: JsonFramework]: JsonFramework[F] = implicitly[JsonFramework[F]]
}
