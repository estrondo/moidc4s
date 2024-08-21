package one.estrondo.oidc4s

trait JwtFramework[F[_], J] {

  def apply(token: String, keyDescription: Option[KeyDescription]): F[J]
}
