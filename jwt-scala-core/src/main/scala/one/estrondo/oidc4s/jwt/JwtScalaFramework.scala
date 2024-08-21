package one.estrondo.oidc4s.jwt

import java.security.PublicKey
import javax.crypto.SecretKey
import one.estrondo.oidc4s.Context
import one.estrondo.oidc4s.JwaAlgorithm
import one.estrondo.oidc4s.JwtFramework
import one.estrondo.oidc4s.KeyDescription
import one.estrondo.oidc4s.OidcException
import pdi.jwt.JwtAlgorithm
import pdi.jwt.JwtOptions
import pdi.jwt.algorithms.JwtAsymmetricAlgorithm
import pdi.jwt.algorithms.JwtHmacAlgorithm
import scala.util.Failure
import scala.util.Success
import scala.util.Try

private[jwt] class JwtScalaFramework[J: JwtScalaJsonLibrary, F[_]: Context](options: JwtOptions)
    extends JwtFramework[F, J] {

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

  private def asymmetricAlgorithm(algorithm: JwaAlgorithm): Try[JwtAsymmetricAlgorithm] = Try {
    algorithm match {
      case JwaAlgorithm.Es256 => JwtAlgorithm.ES256
      case JwaAlgorithm.Rs256 => JwtAlgorithm.RS256
      case JwaAlgorithm.Es384 => JwtAlgorithm.ES384
      case JwaAlgorithm.Rs384 => JwtAlgorithm.RS384
      case JwaAlgorithm.Es512 => JwtAlgorithm.ES512
      case JwaAlgorithm.Rs512 => JwtAlgorithm.RS512
      case unsupported        => throw new OidcException.UnsupportedAlgorithm(unsupported.name)
    }
  }

  private def hMacAlgorithm(algorithm: JwaAlgorithm): Try[JwtHmacAlgorithm] = Try {
    algorithm match {
      case JwaAlgorithm.Hs256 => JwtAlgorithm.HS256
      case JwaAlgorithm.Hs384 => JwtAlgorithm.HS384
      case JwaAlgorithm.Hs512 => JwtAlgorithm.HS512
      case unsupported        => throw new OidcException.UnsupportedAlgorithm(unsupported.name)
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
