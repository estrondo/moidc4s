package one.estrondo.moidc4s
import one.estrondo.moidc4s.syntax._

private[moidc4s] object JwkSetSource {

  abstract private class Remote[F[_]: Transporter: JsonFramework] extends Source[F, JwkSet] {

    protected def extractFromJwkSetUrl(url: String)(implicit ctx: Context[F]): F[JwkSet] = {
      Transporter[F]
        .get(url)
        .flatMap(parseJwkSetResponse(_, url))
    }

    private def parseJwkSetResponse(response: Transporter.Response, url: String)(implicit
        ctx: Context[F],
    ): F[JwkSet] = {
      response match {
        case Transporter.Ok(body) =>
          JsonFramework[F]
            .jwkSet(body)
            .mapError(new OidcException.InvalidJwk("Unable to parse the JwkSet body.", _))

        case response: Transporter.Failed =>
          ctx.failed(new OidcException.FailedRequest(s"Invalid response.", url, response))
      }
    }
  }

  private class JwkSetUri[F[_]: Transporter: JsonFramework](url: String) extends Remote[F] {
    override def apply()(implicit ctx: Context[F]): F[JwkSet] = {
      extractFromJwkSetUrl(url)
    }
  }

  private class Discovery[F[_]: Transporter: JsonFramework](url: String) extends Remote[F] {
    override def apply()(implicit ctx: Context[F]): F[JwkSet] = {
      Transporter[F]
        .get(url)
        .mapError(new OidcException.Unexpected(s"Unable to perform the request: $url.", _))
        .flatMap(parseDiscoveryResponse)
        .flatMap(extractJwkSetUrl)
        .flatMap(extractFromJwkSetUrl)
    }

    private def parseDiscoveryResponse(response: Transporter.Response)(implicit ctx: Context[F]): F[Metadata] = {
      response match {
        case Transporter.Ok(body)         =>
          JsonFramework[F].metadata(body)
        case response: Transporter.Failed =>
          ctx.failed(new OidcException.FailedRequest("Invalid request.", url, response))
      }
    }

    private def extractJwkSetUrl(metadata: Metadata)(implicit ctx: Context[F]): F[String] = {
      metadata.jwks_uri match {
        case Some(url) => ctx.pure(url)
        case None      => ctx.failed(new OidcException.InvalidMetadata("The parameter 'jwks_uri' is required."))
      }
    }
  }

  def apply[F[_]](provider: Provider.JwkSetProvider[F]): Source[F, JwkSet] = {
    provider match {
      case p @ Provider.Discovery(url)    => new Discovery[F](url)(p.t, p.j)
      case p @ Provider.RemoteJwkSet(url) => new JwkSetUri[F](url)(p.t, p.j)
    }
  }

}
