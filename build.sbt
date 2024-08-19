val scala213 = "2.13.14"
val scala3   = "3.4.2"
val scala12  = "2.12.19"

val supportedScalaVersions = scala213 :: scala3 :: scala12 :: Nil

val Build = new {
  val isSnapshot = true
}

val Versions = new {
  val scalaTest      = "3.2.19"
  val scalaMock      = "6.0.0"
  val zio            = "2.1.6"
  val zioHttp        = "3.0.0-RC9"
  val testContainers = "0.41.4"
  val zioJson        = "0.7.2"
  val jwtScala       = "10.0.1"
  val catsEffect     = "3.5.4"
  val http4s         = "0.23.27"
  val circe          = "0.14.9"
}

inThisBuild(
  Seq(
    scalaVersion := scala3,
    organization := "one.estrondo",
    version      := "0.1.0",
  ),
)

val crossScalacOptions = Def.task {
  CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((3, _))  => Seq("-source:3.0-migration", "-Wunused:all", "-explain")
    case Some((2, 12)) => Seq("-Ywarn-unused", "-Ypartial-unification")
    case Some((2, 13)) => Seq("-Wunused")
    case _             => Seq.empty
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
    zioHttp,
    zioJson,
    jwtScalaCore,
    jwtScalaZio,
    catsEffect,
    http4s,
    circe,
  )

lazy val core = (project in file("core"))
  .settings(
    name                                   := "oidc4s-core",
    crossScalaVersions                     := supportedScalaVersions,
    isSnapshot                             := Build.isSnapshot,
    scalacOptions ++= crossScalacOptions.value,
    libraryDependencies += "org.scalatest" %% "scalatest" % Versions.scalaTest % Test,
    libraryDependencies += "org.scalamock" %% "scalamock" % Versions.scalaMock % Test,
  )

lazy val zio = (project in file("zio"))
  .settings(
    name               := "oidc4s-zio",
    crossScalaVersions := supportedScalaVersions,
    isSnapshot         := Build.isSnapshot,
    scalacOptions ++= crossScalacOptions.value,
    libraryDependencies ++= Seq(
      "dev.zio"      %% "zio"                       % Versions.zio,
      "dev.zio"      %% "zio-test"                  % Versions.zio            % Test,
      "dev.zio"      %% "zio-test-sbt"              % Versions.zio            % Test,
      "com.dimafeng" %% "testcontainers-scala-core" % Versions.testContainers % Test,
    ),
  )
  .dependsOn(
    core % "compile->compile;test->test",
  )

lazy val zioHttp = (project in file("zio-http"))
  .settings(
    name                     := "oidc4s-zio-http",
    crossScalaVersions       := supportedScalaVersions,
    isSnapshot               := Build.isSnapshot,
    Test / parallelExecution := false,
    scalacOptions ++= crossScalacOptions.value,
    libraryDependencies ++= Seq(
      "dev.zio"      %% "zio-http"                      % Versions.zioHttp,
      "com.dimafeng" %% "testcontainers-scala-wiremock" % Versions.testContainers % Test,
      "dev.zio"      %% "zio-test"                      % Versions.zio            % Test,
      "dev.zio"      %% "zio-test-sbt"                  % Versions.zio            % Test,
      "org.slf4j"     % "slf4j-nop"                     % "2.0.13"                % Test,
    ),
  )
  .dependsOn(
    core % "compile->compile;test->test",
    zio  % "compile->compile;test->test",
  )

lazy val zioJson = (project in file("zio-json"))
  .settings(
    name               := "oidc4s-zio-json",
    crossScalaVersions := supportedScalaVersions,
    isSnapshot         := Build.isSnapshot,
    scalacOptions ++= crossScalacOptions.value,
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio-json" % Versions.zioJson,
    ),
  )
  .dependsOn(
    core % "compile->compile;test->test",
    zio  % "compile->compile;test->test",
  )

lazy val jwtScalaCore = (project in file("jwt-scala-core"))
  .settings(
    name               := "oidc4s-jwt-scala-core",
    crossScalaVersions := supportedScalaVersions,
    scalacOptions ++= crossScalacOptions.value,
    isSnapshot         := Build.isSnapshot,
    libraryDependencies ++= Seq(
      "com.github.jwt-scala" %% "jwt-core" % Versions.jwtScala,
    ),
  )
  .dependsOn(
    core % "compile->compile;test->test",
  )

lazy val jwtScalaZio = (project in file("jwt-scala-zio"))
  .settings(
    name               := "oidc4s-jwt-scala-zio",
    crossScalaVersions := supportedScalaVersions,
    scalacOptions ++= crossScalacOptions.value,
    libraryDependencies ++= Seq(
      "com.github.jwt-scala" %% "jwt-zio-json" % Versions.jwtScala,
    ),
  )
  .dependsOn(
    zio          % "compile->compile;test->test",
    jwtScalaCore % "compile->compile;test->test",
  )

lazy val catsEffect = (project in file("cats-effect"))
  .settings(
    name               := "oidc4s-cats-effect",
    crossScalaVersions := supportedScalaVersions,
    isSnapshot         := Build.isSnapshot,
    scalacOptions ++= crossScalacOptions.value,
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-effect"                   % Versions.catsEffect,
      "org.typelevel" %% "cats-effect-testing-scalatest" % "1.5.0" % Test,
    ),
  )
  .dependsOn(
    core % "compile->compile;test->test",
  )

lazy val http4s = (project in file("http4s"))
  .settings(
    name               := "oidc4s-http4s",
    crossScalaVersions := supportedScalaVersions,
    isSnapshot         := Build.isSnapshot,
    scalacOptions ++= crossScalacOptions.value,
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-client" % Versions.http4s,
    ),
  )
  .dependsOn(
    catsEffect % "compile->compile;test->test",
  )

lazy val circe = (project in file("circe"))
  .settings(
    name               := "oidc4s-circe",
    crossScalaVersions := supportedScalaVersions,
    isSnapshot         := Build.isSnapshot,
    scalacOptions ++= crossScalacOptions.value,
    libraryDependencies ++= Seq(
      "io.circe" %% "circe-core"    % Versions.circe,
      "io.circe" %% "circe-generic" % Versions.circe,
      "io.circe" %% "circe-parser"  % Versions.circe,
    ),
  )
  .dependsOn(
    catsEffect % "compile->compile;test->test",
  )

lazy val jwtCirce = (project in file("jwt-scala-circe"))
  .settings(
    name               := "oidc4s-jwt-scala-circe",
    crossScalaVersions := supportedScalaVersions,
    isSnapshot         := Build.isSnapshot,
    scalacOptions ++= crossScalacOptions.value,
    libraryDependencies ++= Seq(
      "com.github.jwt-scala" %% "jwt-circe" % Versions.jwtScala,
    ),
  )
  .dependsOn(
    jwtScalaCore % "compile->compile;test->test",
  )
