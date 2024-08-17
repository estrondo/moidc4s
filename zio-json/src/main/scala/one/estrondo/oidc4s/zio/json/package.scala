package one.estrondo.oidc4s.zio

import one.estrondo.oidc.JsonFramework
import one.estrondo.oidc.Jwk
import one.estrondo.oidc.JwkSet
import one.estrondo.oidc.JwtHeader
import one.estrondo.oidc.Metadata
import zio.ZIO
import zio.json.DeriveJsonDecoder
import zio.json.JsonDecoder

package object json {

  // noinspection SpellCheckingInspection
  type JsonZIO[A] = ZIO[Any, Throwable, A]

  implicit val Implicit: JsonFramework[JsonZIO] = ZIOJsonFramework

  implicit val metadataDecoder: JsonDecoder[Metadata] = DeriveJsonDecoder.gen[Metadata]

  implicit val jwkDecoder: JsonDecoder[Jwk] = DeriveJsonDecoder.gen[Jwk]

  implicit val jwkSetDecoder: JsonDecoder[JwkSet] = DeriveJsonDecoder.gen[JwkSet]

  implicit val jwtHeaderDecoder: JsonDecoder[JwtHeader] = DeriveJsonDecoder.gen[JwtHeader]
}
