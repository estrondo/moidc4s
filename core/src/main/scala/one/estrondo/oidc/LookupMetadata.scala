package one.estrondo.oidc

import syntax._

class LookupMetadata[F[_]: Context: Transporter: Json](provider: Provider) extends Lookup[F, Metadata] {

  override def apply(): F[Metadata] = provider match {
    case Provider.Discovery(url) => discover(url)
  }

  private def discover(url: String): F[Metadata] = {
    for {
      response <- Transporter[F]
                    .get(url)
                    .mapError(new OidcException.Unexpected("Unable to perform the request.", _))
      outcome  <- response match {
                    case Transporter.Ok(body)       => parseMetadataJson(body)
                    case failed: Transporter.Failed => reportFailedDiscoveryRequest(url, failed)
                  }
    } yield {
      outcome
    }
  }

  private def parseMetadataJson(body: String): F[Metadata] =
    Json[F]
      .metadata(body)
      .mapError(new OidcException.Unexpected("Unable to parse the metadata.", _))

  private def reportFailedDiscoveryRequest(url: String, response: Transporter.Failed): F[Metadata] =
    Context[F].failed(
      new OidcException.FailedRequest(
        "The request went wrong.",
        status = response.status,
        headers = response.headers,
        url = url,
        body = response.body,
      ),
    )

  override def invalidate(): F[Unit] = Context[F].done
}
