package one.estrondo.oidc

import one.estrondo.oidc.syntax._

trait OpenIdProvider[F[_]] {

  def evaluate[J](token: String)(implicit jwt: JwtFramework[F, J], ctx: Context[F]): F[J]
}

object OpenIdProvider {

  def apply[F[_]: Context: Ref.Maker](provider: Provider[F]): F[OpenIdProvider[F]] = {
    for {
      keySetCache <- Cache(KeySetLookup(provider))
    } yield {
      new Impl(keySetCache)
    }
  }

  private class Impl[F[_]](keySetCache: Cache[F, KeySet]) extends OpenIdProvider[F] {

    override def evaluate[J](token: String)(implicit jwt: JwtFramework[F, J], ctx: Context[F]): F[J] = {
      for {
        keySet <- keySetCache.get
      } yield {
        ???
      }
    }
  }
}
