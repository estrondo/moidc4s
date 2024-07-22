package one.estrondo.oidc

sealed trait Provider

object Provider {

  case class Discovery(url: String) extends Provider
}
