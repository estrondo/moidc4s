package one.estrondo.oidc

case class JwaAlg(
    value: String,
    alg: Option[JwaAlg.Algorithm],
)

object JwaAlg {

  val Hs256 = new JwaAlg("HS256", Some(Mac("HmacSHA256")))
  val Hs384 = new JwaAlg("HS384", Some(Mac("HmacSHA384")))
  val Hs512 = new JwaAlg("HS512", Some(Mac("HmacSHA512")))
  val Rs256 = new JwaAlg("RS256", Some(DigitalSignature("SHA256withRSA")))
  val Rs384 = new JwaAlg("RS384", Some(DigitalSignature("SHA384withRSA")))
  val Rs512 = new JwaAlg("RS512", Some(DigitalSignature("SHA512withRSA")))
  val Es256 = new JwaAlg("ES256", Some(DigitalSignature("SHA256withECDSA")))
  val Es384 = new JwaAlg("ES384", Some(DigitalSignature("SHA384withECDSA")))
  val Es512 = new JwaAlg("ES512", Some(DigitalSignature("SHA512withECDSA")))
  val None  = new JwaAlg("none", scala.None)

  def read(jwk: Jwk): Option[JwaAlg] = {
    for (alg <- jwk.alg) yield alg match {
      case "HS256"           => Hs256
      case "HS384"           => Hs384
      case "HS512"           => Hs512
      case "RS256"           => Rs256
      case "RS384"           => Rs384
      case "RS512"           => Rs512
      case "ES256"           => Es256
      case "ES384"           => Es384
      case "ES512"           => Es512
      case "none"            => None
      case _ if alg.nonEmpty => JwaAlg(alg, scala.None)
      case _                 => throw new OidcException.InvalidJwk("The 'alg' parameter is empty.")
    }
  }

  sealed trait Algorithm

  case class Mac(value: String) extends Algorithm

  case class DigitalSignature(value: String) extends Algorithm
}
