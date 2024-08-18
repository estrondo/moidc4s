package one.estrondo.oidc4s.jwt

import java.security.PublicKey
import javax.crypto.SecretKey
import one.estrondo.oidc.Context
import one.estrondo.oidc.JwaAlg
import one.estrondo.oidc.JwtFramework
import one.estrondo.oidc.KeyDescription
import one.estrondo.oidc.OidcException
import pdi.jwt.JwtAlgorithm
import pdi.jwt.JwtOptions
import pdi.jwt.algorithms.JwtAsymmetricAlgorithm
import pdi.jwt.algorithms.JwtHmacAlgorithm
import scala.util.Failure
import scala.util.Success
import scala.util.Try

private[jwt] class JwtScalaFramework[J: JwtScalaJsonLibrary, F[_]: Context](options: JwtOptions) extends JwtFramework[F, J] {

  override def apply(token: String, keyDescription: Option[KeyDescription]): F[J] = {
    keyDescription match {
      case Some(keyDescription) => apply(token, keyDescription)
      case None                 => apply(token)
    }
  }

  private def apply(token: String, description: KeyDescription): F[J] = {
    description.key match {
      case KeyDescription.Public(key) => apply(token, key, description.alg.map(asymmetricAlgorithm))
      case KeyDescription.Secret(key) => apply(token, key, description.alg.map(hMacAlgorithm))
    }
  }

  private def asymmetricAlgorithm(jwaAlg: JwaAlg) = Try {
    jwaAlg match {
      case JwaAlg.Es256 => JwtAlgorithm.ES256
      case JwaAlg.Rs256 => JwtAlgorithm.RS256
      case JwaAlg.Es384 => JwtAlgorithm.ES384
      case JwaAlg.Rs384 => JwtAlgorithm.RS384
      case JwaAlg.Es512 => JwtAlgorithm.ES512
      case JwaAlg.Rs512 => JwtAlgorithm.RS512
      case other        => throw new OidcException.UnsupportedAlgorithm(other.value)
    }
  }

  private def hMacAlgorithm(jwaAlg: JwaAlg) = Try {
    jwaAlg match {
      case JwaAlg.Hs256 => JwtAlgorithm.HS256
      case JwaAlg.Hs384 => JwtAlgorithm.HS384
      case JwaAlg.Hs512 => JwtAlgorithm.HS512
      case other        => throw new OidcException.UnsupportedAlgorithm(other.value)
    }
  }

  private def apply(token: String): F[J] = {
    Context[F].fromTry(JwtScalaJsonLibrary[J].decode(token, options))
  }

  private def apply(token: String, key: PublicKey, algorithm: Option[Try[JwtAsymmetricAlgorithm]]): F[J] = {
    algorithm match {
      case Some(Success(algorithm)) => Context[F].fromTry(JwtScalaJsonLibrary[J].decode(token, key, algorithm, options))
      case None                     => Context[F].fromTry(JwtScalaJsonLibrary[J].decode(token, key, options))
      case Some(Failure(cause))     => Context[F].failed(cause)
    }
  }

  private def apply(token: String, key: SecretKey, algorithm: Option[Try[JwtHmacAlgorithm]]): F[J] = {
    algorithm match {
      case Some(Success(algorithm)) => Context[F].fromTry(JwtScalaJsonLibrary[J].decode(token, key, algorithm, options))
      case None                     => Context[F].fromTry(JwtScalaJsonLibrary[J].decode(token, key, options))
      case Some(Failure(cause))     => Context[F].failed(cause)
    }
  }
}
