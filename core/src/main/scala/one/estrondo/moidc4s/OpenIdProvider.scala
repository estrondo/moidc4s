package one.estrondo.moidc4s

import one.estrondo.moidc4s.syntax._

trait OpenIdProvider[F[_]] {

  def evaluate[J](jwtFramework: JwtFramework[F, J])(token: String)(implicit ctx: Context[F]): F[J]
}

object OpenIdProvider {

  def apply[F[_]: Context: JsonFramework: Ref.Maker](provider: Provider[F]): F[OpenIdProvider[F]] = {
    for {
      keySetCache <- Cache(KeySetLookup(provider))
    } yield {
      new Impl(keySetCache)
    }
  }

  private class Impl[F[_]: JsonFramework](keySetCache: Cache[F, KeySet]) extends OpenIdProvider[F] {

    override def evaluate[J](jwtFramework: JwtFramework[F, J])(token: String)(implicit ctx: Context[F]): F[J] = {
      for {
        header         <- HeaderExtractor(token)
        keySet         <- keySetCache.get
        keyDescription <- KeyFinder(header, keySet)
        output         <- jwtFramework(token, keyDescription)
      } yield {
        output
      }
    }
  }
}
