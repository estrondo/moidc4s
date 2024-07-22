package one.estrondo.oidc

trait Json[F[_]] {

  def metadata(body: String): F[Metadata]

  def jwkSet(body: String): F[JwkSet]
}

object Json {

  @inline def apply[F[_]: Json]: Json[F] = implicitly[Json[F]]
}
