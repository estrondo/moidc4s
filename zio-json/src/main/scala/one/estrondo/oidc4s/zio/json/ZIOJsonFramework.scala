package one.estrondo.oidc4s.zio.json

import one.estrondo.oidc.JsonFramework
import one.estrondo.oidc.JwkSet
import one.estrondo.oidc.JwtHeader
import one.estrondo.oidc.Metadata
import one.estrondo.oidc.OidcException
import zio.ZIO
import zio.json._

object ZIOJsonFramework extends JsonFramework[JsonZIO] {

  override def metadata(body: String): JsonZIO[Metadata] = {
    ZIO
      .fromEither(body.fromJson[Metadata])
      .mapError(mapError)
  }

  override def jwkSet(body: String): JsonZIO[JwkSet] = {
    ZIO
      .fromEither(body.fromJson[JwkSet])
      .mapError(mapError)
  }

  override def jwtHeader(body: String): JsonZIO[JwtHeader] = {
    ZIO
      .fromEither(body.fromJson[JwtHeader])
      .mapError(mapError)
  }

  private def mapError(message: String): OidcException =
    new ZIOJsonException(message)
}
