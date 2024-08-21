package one.estrondo.oidc4s

import java.security.AlgorithmParameters
import java.security.KeyFactory
import java.security.spec.ECGenParameterSpec
import java.security.spec.ECParameterSpec
import java.security.spec.ECPoint
import java.security.spec.ECPublicKeySpec
import java.security.spec.RSAPublicKeySpec
import javax.crypto.spec.SecretKeySpec
import scala.util.Try

private[oidc4s] object Jwa {

  def apply(kty: String, jwk: Jwk): Try[KeyDescription] = Try {
    if (jwk.use.exists(_ != "sig")) {
      throw new OidcException.InvalidJwk(s"The 'use' parameter should be 'sig' not ${jwk.use.get}.")
    }

    val alg = JwaAlgorithm.extract(jwk)

    val key = kty match {
      case "EC"  => ec(jwk)
      case "RSA" => rsa(jwk)
      case "oct" => oct(jwk, alg)
      case _     => throw new OidcException.InvalidJwk(s"Unsupported kty: $kty.")
    }

    KeyDescription(
      kid = jwk.kid,
      key = key,
      alg = alg,
    )
  }

  private def ec(jwk: Jwk): KeyDescription.Key = {
    val crv = jwk.crv match {
      case Some("P-256") => "secp256r1"
      case Some("P-384") => "secp384r1"
      case Some("P-521") => "secp521r1"
      case Some(other)   => throw new OidcException.InvalidJwk(s"Unsupported EC Curve $other.")
      case None          => throw new OidcException.InvalidJwk("The parameter 'crv' is required for this key.")
    }

    val param = AlgorithmParameters.getInstance("EC")
    param.init(new ECGenParameterSpec(crv))

    val x = B64.decodeUrlEncodedAsBigInteger("x", jwk.x)
    val y = B64.decodeUrlEncodedAsBigInteger("y", jwk.y)

    val spec = new ECPublicKeySpec(
      new ECPoint(x, y),
      param.getParameterSpec(classOf[ECParameterSpec]),
    )

    KeyDescription.Public(keyFactory("EC").generatePublic(spec))
  }

  private def rsa(jwk: Jwk): KeyDescription.Key = {
    val n = B64.decodeUrlEncodedAsBigInteger("n", jwk.n)
    val e = B64.decodeUrlEncodedAsBigInteger("e", jwk.e)

    val spec = new RSAPublicKeySpec(n, e)
    KeyDescription.Public(keyFactory("RSA").generatePublic(spec))
  }

  private def keyFactory(algorithm: String): KeyFactory = {
    KeyFactory.getInstance(algorithm)
  }

  private def oct(jwk: Jwk, algorithm: Option[JwaAlgorithm]): KeyDescription.Key = {
    algorithm match {
      case Some(JwaAlgorithm.Hmac(_, fullName, _)) =>
        val k             = B64.decodeUrlEncoded("k", jwk.k)
        val secretKeySpec = new SecretKeySpec(k, fullName)
        KeyDescription.Secret(secretKeySpec)

      case _ =>
        throw new OidcException.InvalidJwk("Invalid algorithm!")
    }

  }
}
