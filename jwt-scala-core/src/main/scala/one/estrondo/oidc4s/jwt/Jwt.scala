package one.estrondo.oidc4s.jwt

import one.estrondo.oidc.Context
import one.estrondo.oidc.JwtFramework
import pdi.jwt.JwtOptions

object Jwt {

  def decode[J: JwtScalaJsonLibrary, F[_]: Context](options: JwtOptions = JwtOptions.DEFAULT): JwtFramework[F, J] =
    new JwtScalaFramework(options)
}
