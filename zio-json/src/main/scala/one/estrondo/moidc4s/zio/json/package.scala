package one.estrondo.moidc4s.zio

import one.estrondo.moidc4s.JsonFramework
import one.estrondo.moidc4s.Jwk
import one.estrondo.moidc4s.JwkSet
import one.estrondo.moidc4s.JwtHeader
import one.estrondo.moidc4s.Metadata
import one.estrondo.moidc4s.OidcException
import zio.ZIO
import zio.json._
import zio.json.JsonDecoder

package object json {

  implicit val metadataDecoder: JsonDecoder[Metadata] = DeriveJsonDecoder.gen[Metadata]

  implicit val jwkDecoder: JsonDecoder[Jwk] = DeriveJsonDecoder.gen[Jwk]

  implicit val jwkSetDecoder: JsonDecoder[JwkSet] = DeriveJsonDecoder.gen[JwkSet]

  implicit val jwtHeaderDecoder: JsonDecoder[JwtHeader] = DeriveJsonDecoder.gen[JwtHeader]

  implicit object ZIOJsonFramework extends JsonFramework[OZIO] {

    override def metadata(body: String): OZIO[Metadata] = {
      ZIO
        .fromEither(body.fromJson[Metadata])
        .mapError(mapError)
    }

    override def jwkSet(body: String): OZIO[JwkSet] = {
      ZIO
        .fromEither(body.fromJson[JwkSet])
        .mapError(mapError)
    }

    override def jwtHeader(body: String): OZIO[JwtHeader] = {
      ZIO
        .fromEither(body.fromJson[JwtHeader])
        .mapError(mapError)
    }

    private def mapError(message: String): OidcException =
      new ZIOJsonException(message)
  }
}
