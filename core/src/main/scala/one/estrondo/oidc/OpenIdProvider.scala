package one.estrondo.oidc

import one.estrondo.oidc.syntax._

trait OpenIdProvider[F[_]] {

  def evaluate[J](token: String)(implicit jwt: Jwt[F, J]): F[J]
}

object OpenIdProvider {

  def apply[F[_]: Context: RefMaker: Transporter: Json](provider: Provider): F[OpenIdProvider[F]] = {
    for {
      metadataCache <- Cache[F, Metadata](new LookupMetadata[F](provider))
      keySetCache   <- Cache[F, KeySet](new LookupKeySet[F](metadataCache))
    } yield {
      new Impl(keySetCache)
    }
  }

  private class Impl[F[_]: Context: RefMaker: Transporter: Json](keySetCache: Cache[F, KeySet])
      extends OpenIdProvider[F] {

    override def evaluate[J](token: String)(implicit jwt: Jwt[F, J]): F[J] = {
      for {
        jwkSetT <- keySetCache.get
      } yield {
        ???
      }
    }
  }
}
