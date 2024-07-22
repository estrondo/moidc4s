package one.estrondo.oidc

import java.security.Key
import java.security.PrivateKey
import java.security.PublicKey
import java.security.Signature
import java.util
import javax.crypto.Mac

object SignatureOperations {

  def verify(alg: JwaAlg, data: Array[Byte], expected: Array[Byte], key: Key): Boolean = {
    alg.alg match {
      case Some(JwaAlg.Mac(value))              =>
        val mac  = Mac.getInstance(value)
        mac.init(key)
        val hash = mac.doFinal(data)
        util.Arrays.equals(hash, hash)
      case Some(JwaAlg.DigitalSignature(value)) =>
        val signature = Signature.getInstance(value)
        signature.initVerify(key.asInstanceOf[PublicKey])
        signature.update(data)
        signature.verify(expected)
      case _                                    => ???
    }
  }

  def sign(alg: JwaAlg, data: Array[Byte], key: Key): Array[Byte] = {
    alg.alg match {
      case Some(JwaAlg.Mac(value))              =>
        val mac = Mac.getInstance(value)
        mac.init(key)
        mac.doFinal(data)
      case Some(JwaAlg.DigitalSignature(value)) =>
        val signature = Signature.getInstance(value)
        signature.initSign(key.asInstanceOf[PrivateKey])
        signature.update(data)
        signature.sign()
      case _                                    => ???
    }
  }
}
