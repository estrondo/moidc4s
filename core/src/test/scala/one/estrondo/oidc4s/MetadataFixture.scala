package one.estrondo.oidc4s

object MetadataFixture {

  def createRandom(): Metadata = Metadata(
    jwks_uri = Some(Fixtures.randomId()),
  )
}
