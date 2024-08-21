package one.estrondo.oidc4s

import java.util.UUID
import scala.util.Random

object Fixtures {
  def pickOne[T](values: T*): T = values(Random.nextInt(values.size))

  def randomId(): String = UUID.randomUUID().toString
}
