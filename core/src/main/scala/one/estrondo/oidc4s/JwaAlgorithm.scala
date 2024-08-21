package one.estrondo.oidc4s

import scala.collection.immutable.HashSet

sealed trait JwaAlgorithm {
  def name: String
  override def hashCode(): Int = name.hashCode
}

object JwaAlgorithm {

  val Hs256: Hmac = Hmac("HS256", "HmacSHA256", 256)
  val Hs384: Hmac = Hmac("HS384", "HmacSHA384", 384)
  val Hs512: Hmac = Hmac("HS512", "HmacSHA512", 512)
  val Rs256: Rsa  = Rsa("RS256", "SHA256withRSA")
  val Rs384: Rsa  = Rsa("RS384", "SHA384withRSA")
  val Rs512: Rsa  = Rsa("RS512", "SHA512withRSA")
  val Es256: Ec   = Ec("ES256", "SHA256withECDSA", "P-256", "secp256r1")
  val Es384: Ec   = Ec("ES384", "SHA384withECDSA", "P-384", "secp384r1")
  val Es512: Ec   = Ec("ES512", "SHA512withECDSA", "P-521", "secp521r1")

  case class Rsa(name: String, fullName: String) extends JwaAlgorithm

  case class Ec(name: String, fullName: String, curve: String, curveFullName: String) extends JwaAlgorithm

  case class Hmac(name: String, fullName: String, length: Int) extends JwaAlgorithm

  case class Other(name: String, jwk: Jwk) extends JwaAlgorithm

  case object None extends JwaAlgorithm {
    val name: String = "none"
  }

  def all: Set[JwaAlgorithm] = HashSet(
    Hs256,
    Hs384,
    Hs512,
    Rs256,
    Rs384,
    Rs512,
    Es256,
    Es384,
    Es512,
  )

  private[oidc4s] def extract(jwk: Jwk): Option[JwaAlgorithm] = {
    for (algorithmName <- jwk.alg) yield {
      algorithmName match {
        case "HS256"                     => Hs256
        case "HS384"                     => Hs384
        case "HS512"                     => Hs512
        case "RS256"                     => Rs256
        case "RS384"                     => Rs384
        case "RS512"                     => Rs512
        case "ES256"                     => Es256
        case "ES384"                     => Es384
        case "ES512"                     => Es512
        case "none"                      => None
        case _ if algorithmName.nonEmpty => Other(algorithmName, jwk)
        case _                           => throw new OidcException.InvalidJwk("The 'alg' parameter is empty.")
      }
    }
  }
}
