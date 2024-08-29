package one.estrondo.moidc4s

sealed trait Provider[F[_]]

object Provider {

  sealed private[moidc4s] trait JwkSetProvider[F[_]] extends Provider[F]

  sealed private[moidc4s] trait KeySetProvider[F[_]] extends Provider[F]

  case class ExternalKeySet[F[_]](source: Source[F, KeySet]) extends KeySetProvider[F]

  case class Discovery[F[_]](url: String)(implicit val t: Transporter[F], val j: JsonFramework[F])
      extends JwkSetProvider[F]

  case class RemoteJwkSet[F[_]](url: String)(implicit val t: Transporter[F], val j: JsonFramework[F])
      extends JwkSetProvider[F]

}
