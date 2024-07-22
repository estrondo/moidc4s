package one.estrondo.oidc

import scala.util.Random

object MetadataFixture {

  def createRandom(): Metadata = Metadata(
    jwksUri = Some(Random.nextString(32)),
  )
}
