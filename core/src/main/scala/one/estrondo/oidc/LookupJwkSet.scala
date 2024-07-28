package one.estrondo.oidc

import syntax._

object LookupJwkSet {

  class External[F[_]: Context](provider: Provider.ExternalJwkSet[F]) extends Lookup[F, JwkSet] {

    override def apply(): F[JwkSet] = {
      provider.jwkSet
        .mapError(new OidcException.Unexpected("Unable to acquire the JwkSet.", _))
    }

    override def invalidate(): F[Unit] =
      Context[F].done
  }

  class FromRemote[F[_]: Context](provider: Provider.RemoteJwkSetProvider[F]) extends Lookup[F, JwkSet] {

    override def apply(): F[JwkSet] = {
      provider match {
        case provider: Provider.Discovery[F] =>
          import provider._
          Transporter[F]
            .get(url)
            .mapError(new OidcException.Unexpected(s"Unable to perform the request: $url", _))
            .flatMap(fromDiscoveryResponse(url, _))
        case provider: Provider.JwkSetUri[F] =>
          import provider._
          fromJwkSetUrl(url)
      }
    }

    private def fromDiscoveryResponse(
        url: String,
        response: Transporter.Response,
    )(implicit a: Transporter[F], b: Json[F]): F[JwkSet] = {
      response match {
        case Transporter.Ok(body) =>
          Json[F]
            .metadata(body)
            .mapError(new OidcException.InvalidMetadata("Unable to read the metadata.", _))
            .flatMap(fromMetadata)

        case failed: Transporter.Failed =>
          Context[F]
            .failed(new OidcException.FailedRequest("Request to Discovery has failed.", url, failed))
      }
    }

    private def fromMetadata(metadata: Metadata)(implicit a: Transporter[F], b: Json[F]): F[JwkSet] = {
      metadata.jwks_uri match {
        case Some(url) =>
          Transporter[F]
            .get(url)
            .mapError(new OidcException.Unexpected(s"Unable to perform the request: $url.", _))
            .flatMap(fromJwkSetResponse(url, _))

        case None =>
          Context[F].failed(new OidcException.InvalidMetadata("There is no 'jwks_uri' parameter in the Metadata."))
      }
    }

    private def fromJwkSetResponse(url: String, response: Transporter.Response)(implicit a: Json[F]): F[JwkSet] = {
      response match {
        case Transporter.Ok(body) =>
          Json[F]
            .jwkSet(body)
            .mapError(new OidcException.InvalidJwk("Unable to read the JwkSet JSON.", _))

        case failed: Transporter.Failed =>
          Context[F].failed(new OidcException.FailedRequest("Request to JwkSet has failed.", url, failed))
      }
    }

    private def fromJwkSetUrl(url: String)(implicit a: Transporter[F], b: Json[F]): F[JwkSet] = {
      Transporter[F]
        .get(url)
        .mapError(new OidcException.Unexpected(s"Unable to perform the request: $url.", _))
        .flatMap(fromJwkSetResponse(url, _))
    }

    override def invalidate(): F[Unit] =
      Context[F].done
  }

}
