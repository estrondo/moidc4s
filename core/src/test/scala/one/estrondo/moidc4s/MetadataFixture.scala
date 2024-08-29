package one.estrondo.moidc4s

object MetadataFixture {

  def createRandom(): Metadata = Metadata(
    jwks_uri = Some(Fixtures.randomId()),
  )
}
