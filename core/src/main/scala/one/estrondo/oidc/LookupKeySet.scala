package one.estrondo.oidc

import one.estrondo.oidc.syntax._
import scala.collection.immutable.HashMap
import scala.util.Failure
import scala.util.Success
import scala.util.Try

object LookupKeySet {

  class External[F[_]: Context](provider: Provider.ExternalKeySet[F]) extends Lookup[F, KeySet] {

    override def apply(): F[KeySet] = {
      for {
        keySet <- provider.keySet
                    .mapError(new OidcException.Unexpected("Unable to acquire the KeySet.", _))
      } yield keySet
    }

    override def invalidate(): F[Unit] = Context[F].done
  }

  class FromJwkSet[F[_]: Context](underlying: Lookup[F, JwkSet]) extends Lookup[F, KeySet] {

    override def apply(): F[KeySet] = {
      underlying()
        .mapError(new OidcException.Unexpected("Unable to acquire the JwkSet.", _))
        .flatMap { jwkSet =>
          parse(jwkSet) match {
            case Success(keySet) => Context[F].pure(keySet)
            case Failure(cause)  => Context[F].failed(cause)
          }
        }
    }

    private def parse(jwkSet: JwkSet): Try[KeySet] = {
      for {
        descriptions <- jwkSet.keys.tryFoldLeft(Seq.empty[KeyDescription]) { (seq, key) =>
                          for (description <- parse(key)) yield {
                            seq :+ description
                          }
                        }
      } yield {
        var byKid      = HashMap.empty[String, KeyDescription]
        var withoutKid = Seq.empty[KeyDescription]

        for (description <- descriptions) description.kid match {
          case Some(kid) => byKid += (kid -> description)
          case None      => withoutKid :+= description
        }

        KeySet(byKid, withoutKid)
      }
    }

    private def parse(jwk: Jwk): Try[KeyDescription] = {
      jwk.kty match {
        case Some(kty) if kty.nonEmpty =>
          Jwa(kty, jwk)
        case _                         =>
          Failure(new OidcException.InvalidJwk("It is required to have a non-empty 'kty'."))
      }
    }

    override def invalidate(): F[Unit] =
      underlying.invalidate()
  }
}
