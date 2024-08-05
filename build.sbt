val scala213 = "2.13.14"
val scala3   = "3.4.2"
val scala12  = "2.12.19"

val supportedScalaVersions = scala213 :: scala3 :: scala12 :: Nil

val Versions = new {
  val scalaTest = "3.2.19"
  val scalaMock = "6.0.0"
  val zio       = "2.1.6"
}

inThisBuild(
  Seq(
    scalaVersion := scala3,
    organization := "one.estrondo",
    version      := "0.0.1-SNAPSHOT",
  ),
)

val crossScalacOptions = scalacOptions ++= {
  CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((3, _))  => List("-source:3.0-migration", "-Wunused:all", "-explain")
    case Some((2, 12)) => List("-Ywarn-unused")
    case Some((2, 13)) => List("-Wunused")
    case _             => Nil
  }
}

lazy val root = (project in file("."))
  .settings(
    name               := "oidc4",
    crossScalaVersions := Nil,
    publish / skip     := true,
  )
  .enablePlugins(ScalaUnidocPlugin)
  .aggregate(
    core,
    zio,
  )

lazy val core = (project in file("core"))
  .settings(
    name                                   := "oidc4s-core",
    crossScalaVersions                     := supportedScalaVersions,
    isSnapshot                             := true,
    crossScalacOptions,
    libraryDependencies += "org.scalatest" %% "scalatest" % Versions.scalaTest % Test,
    libraryDependencies += "org.scalamock" %% "scalamock" % Versions.scalaMock % Test,
  )

lazy val zio = (project in file("zio"))
  .settings(
    name                             := "oidc4s-zio",
    crossScalaVersions               := supportedScalaVersions,
    isSnapshot                       := true,
    crossScalacOptions,
    libraryDependencies += "dev.zio" %% "zio"          % Versions.zio,
    libraryDependencies += "dev.zio" %% "zio-test"     % Versions.zio % Test,
    libraryDependencies += "dev.zio" %% "zio-test-sbt" % Versions.zio % Test,
  )
  .dependsOn(
    core % "compile->compile;test->test",
  )
