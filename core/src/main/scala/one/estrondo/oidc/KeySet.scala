package one.estrondo.oidc

case class KeySet(
    byKid: Map[String, KeyDescription],
    noKid: Seq[KeyDescription],
)
