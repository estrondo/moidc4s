package one.estrondo.oidc

sealed trait Provider

object Provider {

  sealed private[oidc] trait JwkSetProvider[F[_]] extends Provider

  sealed private[oidc] trait KeySetProvider extends Provider

  sealed private[oidc] trait RemoteJwkSetProvider[F[_]] extends JwkSetProvider[F]

  case class Discovery[F[_]](url: String)(implicit
      val t: Transporter[F],
      val j: Json[F],
  ) extends RemoteJwkSetProvider[F]

  case class JwkSetUri[F[_]: Transporter: Json](url: String)(implicit
      val t: Transporter[F],
      val j: Json[F],
  ) extends RemoteJwkSetProvider[F]

  case class ExternalJwkSet[F[_]](jwkSet: F[JwkSet]) extends JwkSetProvider[F]

  case class ExternalKeySet[F[_]](keySet: F[KeySet]) extends KeySetProvider
}
