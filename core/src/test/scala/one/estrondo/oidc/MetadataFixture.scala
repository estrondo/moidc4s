package one.estrondo.oidc

object MetadataFixture {

  def createRandom(): Metadata = Metadata(
    jwks_uri = Some(Fixtures.randomId()),
  )
}
