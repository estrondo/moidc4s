package one.estrondo.oidc

private[oidc] object KeyFinder {

  def apply[F[_]: Context](header: JwtHeader, keySet: KeySet): F[Option[KeyDescription]] = {
    (header.kid, header.alg) match {
      case (Some(kid), Some(alg)) => byKid(kid, alg, keySet)
      case (Some(kid), None)      => byKid(kid, keySet)
      case (None, Some(alg))      => byAlg(alg, keySet)
      case (None, None)           => useDefault(keySet)
    }
  }

  private def byKid[F[_]: Context](kid: String, alg: String, keySet: KeySet): F[Option[KeyDescription]] = {
    Context[F].pure(
      keySet.byKid.get(kid) match {
        case some @ Some(description) if description.alg.exists(_.name == alg) => some
        case _                                                                 => None
      },
    )
  }

  private def byKid[F[_]: Context](kid: String, keySet: KeySet): F[Option[KeyDescription]] = {
    Context[F].pure(keySet.byKid.get(kid))
  }

  private def byAlg[F[_]: Context](alg: String, keySet: KeySet): F[Option[KeyDescription]] = {
    var found    = Option.empty[KeyDescription]
    var isSingle = true
    val iterator = keySet.iterator

    while (isSingle && iterator.hasNext) {
      val current = iterator.next()
      if (current.alg.exists(_.name == alg)) {
        if (found.isEmpty) {
          found = Some(current)
        } else {
          isSingle = false
        }
      }
    }

    if (isSingle) {
      Context[F].pure(found)
    } else {
      Context[F].failed(new OidcException.AmbiguousException("There is more than one possible key."))
    }
  }

  private def useDefault[F[_]: Context](keySet: KeySet): F[Option[KeyDescription]] = {
    if (keySet.size == 1) {
      Context[F].pure(keySet.headOption)
    } else {
      Context[F].failed(new OidcException.AmbiguousException("There is more than on key."))
    }
  }
}
