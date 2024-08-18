package one.estrondo.oidc

import java.security.Key
import java.security.PrivateKey
import java.security.PublicKey
import java.security.Signature
import javax.crypto.Mac
import org.scalatest.Assertion
import org.scalatest.matchers.should.Matchers._

object SignatureOperations {

  def verify(algorithm: JwaAlgorithm, data: Array[Byte], expected: Array[Byte], key: Key): Assertion = {

    def asymmetric(fullName: String) = {
      val signature = Signature.getInstance(fullName)
      signature.initVerify(key.asInstanceOf[PublicKey])
      signature.update(data)
      signature.verify(expected) should be(true)
    }

    def symmetric(fullName: String) = {
      val mac  = Mac.getInstance(fullName)
      mac.init(key)
      val hash = mac.doFinal(data)
      hash should be(expected)
    }

    algorithm match {
      case JwaAlgorithm.Hmac(_, fullName, _)  => symmetric(fullName)
      case JwaAlgorithm.Rsa(_, fullName)      => asymmetric(fullName)
      case JwaAlgorithm.Ec(_, fullName, _, _) => asymmetric(fullName)
      case _                                  => ???
    }
  }

  def sign(algorithm: JwaAlgorithm, data: Array[Byte], key: Key): Array[Byte] = {

    def symmetric(fullName: String) = {
      val mac = Mac.getInstance(fullName)
      mac.init(key)
      mac.doFinal(data)
    }

    def asymmetric(fullName: String) = {
      val signature = Signature.getInstance(fullName)
      signature.initSign(key.asInstanceOf[PrivateKey])
      signature.update(data)
      signature.sign()
    }

    algorithm match {
      case JwaAlgorithm.Hmac(_, fullName, _)  => symmetric(fullName)
      case JwaAlgorithm.Rsa(_, fullName)      => asymmetric(fullName)
      case JwaAlgorithm.Ec(_, fullName, _, _) => asymmetric(fullName)
      case _                                  => ???
    }
  }
}
