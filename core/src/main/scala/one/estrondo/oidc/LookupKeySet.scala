package one.estrondo.oidc

import scala.collection.immutable.HashMap
import scala.util.Failure
import scala.util.Success
import scala.util.Try
import syntax._

class LookupKeySet[F[_]: Context: Transporter: Json](cache: Cache[F, Metadata]) extends Lookup[F, KeySet] {

  override def apply(): F[KeySet] = {
    for {
      metadata <- cache.get
      jwkSet   <- loadJwkSet(metadata)
    } yield jwkSet
  }

  private def loadJwkSet(metadata: Metadata): F[KeySet] = {

    def parseResponse(url: String)(response: Transporter.Response): F[KeySet] = response match {
      case Transporter.Ok(body)       =>
        Json[F]
          .jwkSet(body)
          .mapError(new OidcException.NoJwkSet("Unable to parse the JWK.", _))
          .flatMap(parseJwkSet)
      case failed: Transporter.Failed =>
        Context[F].failed(
          new OidcException.FailedRequest("Unable to request the jwksSet.", url = url, response = failed),
        )
    }

    metadata.jwksUri match {
      case Some(uri) =>
        Transporter[F]
          .get(uri)
          .mapError(new OidcException.Unexpected(s"Unable to perform the request: $uri.", _))
          .flatMap(parseResponse(uri))
      case None      =>
        Context[F].failed(new OidcException.NoJwkSet())
    }
  }

  private def parseJwkSet(jwkSet: JwkSet): F[KeySet] = {
    jwkSet.keys.tryFoldLeft(Seq.empty[KeyDescription]) { (seq, jwk) =>
      for (key <- parse(jwk)) yield {
        seq :+ key
      }
    } match {
      case Success(keys)  =>
        var byKid      = HashMap.empty[String, KeyDescription]
        var withoutKid = Seq.empty[KeyDescription]

        for (key <- keys) key.kid match {
          case Some(kid) => byKid += (kid -> key)
          case None      => withoutKid :+= key
        }

        Context[F].pure(KeySet(byKid, withoutKid))
      case Failure(cause) =>
        Context[F].failed(cause)
    }
  }

  private def parse(jwk: Jwk): Try[KeyDescription] = {
    jwk.kty match {
      case Some(kty) => Jwa(kty, jwk)
      case None      => Failure(new OidcException.InvalidJwk("The parameter 'kty' is required."))
    }
  }

  override def invalidate(): F[Unit] = ???
}
