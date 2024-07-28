package one.estrondo.oidc

import scala.util.Random

object MetadataFixture {

  def createRandom(): Metadata = Metadata(
    jwks_uri = Some(Random.nextString(32)),
  )
}
