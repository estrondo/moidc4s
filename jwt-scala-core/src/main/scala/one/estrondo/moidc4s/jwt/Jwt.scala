package one.estrondo.moidc4s.jwt

import one.estrondo.moidc4s.Context
import one.estrondo.moidc4s.JwtFramework
import pdi.jwt.JwtOptions

object Jwt {

  def decode[J: JwtScalaJsonLibrary, F[_]: Context](options: JwtOptions = JwtOptions.DEFAULT): JwtFramework[F, J] =
    new JwtScalaFramework(options)
}
