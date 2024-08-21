package one.estrondo.oidc4s.zio

import one.estrondo.oidc4s.JsonFramework
import one.estrondo.oidc4s.Jwk
import one.estrondo.oidc4s.JwkSet
import one.estrondo.oidc4s.JwtHeader
import one.estrondo.oidc4s.Metadata
import one.estrondo.oidc4s.OidcException
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
