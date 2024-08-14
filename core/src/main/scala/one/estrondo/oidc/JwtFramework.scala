package one.estrondo.oidc

trait JwtFramework[F[_], J] {

  def apply(token: String, keyDescription: Option[KeyDescription]): F[J]
}
