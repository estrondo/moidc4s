package one.estrondo.oidc4s

import _root_.cats.effect.IO
import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder
import io.circe.parser.decode
import one.estrondo.oidc4s.JsonFramework
import one.estrondo.oidc4s.Jwk
import one.estrondo.oidc4s.JwkSet
import one.estrondo.oidc4s.JwtHeader
import one.estrondo.oidc4s.Metadata

package object circe {

  implicit val metadataDecoder: Decoder[Metadata] = deriveDecoder[Metadata]

  implicit val jwkDecoder: Decoder[Jwk] = deriveDecoder[Jwk]

  implicit val jwkSetDecoder: Decoder[JwkSet] = deriveDecoder[JwkSet]

  implicit val jwtHeaderDecoder: Decoder[JwtHeader] = deriveDecoder[JwtHeader]

  implicit object CirceJsonFramework extends JsonFramework[IO] {

    override def metadata(body: String): IO[Metadata] =
      IO.defer(IO.fromEither(decode[Metadata](body)))

    override def jwkSet(body: String): IO[JwkSet] =
      IO.defer(IO.fromEither(decode[JwkSet](body)))

    override def jwtHeader(body: String): IO[JwtHeader] =
      IO.defer(IO.fromEither(decode[JwtHeader](body)))
  }
}
