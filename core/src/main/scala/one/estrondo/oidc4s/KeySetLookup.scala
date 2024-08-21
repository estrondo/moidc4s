package one.estrondo.oidc4s
import one.estrondo.oidc4s.syntax._
import scala.collection.immutable.HashMap
import scala.util.Failure
import scala.util.Success
import scala.util.Try

private[oidc4s] object KeySetLookup {

  private[oidc4s] class External[F[_]](source: Source[F, KeySet]) extends Lookup[F, KeySet] {

    override def apply()(implicit ctx: Context[F]): F[KeySet] = {
      source()
    }

    override def invalidate()(implicit ctx: Context[F]): F[Unit] =
      Context[F].done
  }

  private[oidc4s] class FromJwkSet[F[_]](source: Source[F, JwkSet]) extends Lookup[F, KeySet] {

    override def apply()(implicit ctx: Context[F]): F[KeySet] = {
      for {
        jwkSet <- source()
        keySet <- parse(jwkSet)
      } yield {
        keySet
      }
    }

    override def invalidate()(implicit ctx: Context[F]): F[Unit] =
      ctx.done

    private def parse[F[_]: Context](jwkSet: JwkSet): F[KeySet] = {
      jwkSet.keys.tryFoldLeft(Seq.empty[KeyDescription]) { (seq, key) =>
        for (keyDescription <- parse(key)) yield {
          seq :+ keyDescription
        }
      } match {
        case Success(descriptions) =>
          var withKid    = HashMap.empty[String, KeyDescription]
          var withoutKid = Seq.empty[KeyDescription]

          for (description <- descriptions) {
            description.kid match {
              case Some(kid) => withKid += (kid -> description)
              case _         => withoutKid :+= description
            }
          }

          Context[F].pure(KeySet(byKid = withKid, withoutKid = withoutKid))

        case Failure(cause) =>
          Context[F].failed(new OidcException.InvalidJwk("Unable to parse Jwk.", cause))
      }
    }

    private def parse(jwk: Jwk): Try[KeyDescription] = {
      jwk.kty match {
        case Some(kty) => Jwa(kty, jwk)
        case None      => Failure(new OidcException.InvalidJwk("The parameter 'kty' is required."))
      }
    }
  }

  def apply[F[_]](provider: Provider[F]): Lookup[F, KeySet] = {
    provider match {
      case Provider.ExternalKeySet(source) => new External[F](source)
      case p: Provider.JwkSetProvider[F]   => new FromJwkSet[F](JwkSetSource(p))
    }
  }
}
