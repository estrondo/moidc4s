package one.estrondo.moidc4s

case class KeySet(
    byKid: Map[String, KeyDescription],
    withoutKid: Seq[KeyDescription],
) {

  def headOption: Option[KeyDescription] = byKid.values.headOption.orElse(withoutKid.headOption)

  def iterator: Iterator[KeyDescription] = byKid.valuesIterator ++ withoutKid.iterator

  def size: Int = byKid.size + withoutKid.size
}
