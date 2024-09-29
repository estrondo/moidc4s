import scala.language.postfixOps

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
  val weaverCats     = "0.8.4"
  val http4s         = "0.23.27"
  val circe          = "0.14.9"
  val nettyHttp4s    = "0.5.18"
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

val commonSettings: Seq[Def.SettingsDefinition] = Seq(
  crossScalaVersions := supportedScalaVersions,
  isSnapshot         := Build.isSnapshot,
  scalacOptions ++= crossScalacOptions.value,
)

lazy val root = (project in file("."))
  .settings(
    name               := "moidc4s",
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
    jwtScalaCirce,
  )

lazy val core = (project in file("core"))
  .settings(commonSettings *)
  .settings(
    name := "moidc4s-core",
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % Versions.scalaTest % Test,
      "org.scalamock" %% "scalamock" % Versions.scalaMock % Test,
    ),
  )

lazy val zio = (project in file("zio"))
  .settings(commonSettings *)
  .settings(
    name := "moidc4s-zio",
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
  .settings(commonSettings *)
  .settings(
    name                     := "moidc4s-zio-http",
    Test / parallelExecution := false,
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
  .settings(commonSettings *)
  .settings(
    name := "moidc4s-zio-json",
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio-json" % Versions.zioJson,
    ),
  )
  .dependsOn(
    core % "compile->compile;test->test",
    zio  % "compile->compile;test->test",
  )

lazy val jwtScalaCore = (project in file("jwt-scala-core"))
  .settings(commonSettings *)
  .settings(
    name := "moidc4s-jwt-scala-core",
    libraryDependencies ++= Seq(
      "com.github.jwt-scala" %% "jwt-core" % Versions.jwtScala,
    ),
  )
  .dependsOn(
    core % "compile->compile;test->test",
  )

lazy val jwtScalaZio = (project in file("jwt-scala-zio"))
  .settings(commonSettings *)
  .settings(
    name := "moidc4s-jwt-scala-zio",
    libraryDependencies ++= Seq(
      "com.github.jwt-scala" %% "jwt-zio-json" % Versions.jwtScala,
    ),
  )
  .dependsOn(
    zio          % "compile->compile;test->test",
    jwtScalaCore % "compile->compile;test->test",
  )

lazy val catsEffect = (project in file("cats-effect"))
  .settings(commonSettings *)
  .settings(
    name := "moidc4s-cats-effect",
    libraryDependencies ++= Seq(
      "org.typelevel"       %% "cats-effect"                   % Versions.catsEffect,
      "org.typelevel"       %% "cats-effect-testing-scalatest" % "1.5.0"                 % Test,
      "com.disneystreaming" %% "weaver-cats"                   % Versions.weaverCats     % Test,
      "com.dimafeng"        %% "testcontainers-scala-core"     % Versions.testContainers % Test,
    ),
  )
  .dependsOn(
    core % "compile->compile;test->test",
  )

lazy val http4s = (project in file("http4s"))
  .settings(commonSettings *)
  .settings(
    name := "moidc4s-http4s",
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-client" % Versions.http4s,
    ),
  )
  .dependsOn(
    catsEffect % "compile->compile;test->test",
  )

lazy val circe = (project in file("circe"))
  .settings(commonSettings *)
  .settings(
    name := "moidc4s-circe",
    libraryDependencies ++= Seq(
      "io.circe" %% "circe-core"    % Versions.circe,
      "io.circe" %% "circe-generic" % Versions.circe,
      "io.circe" %% "circe-parser"  % Versions.circe,
    ),
  )
  .dependsOn(
    catsEffect % "compile->compile;test->test",
  )

lazy val jwtScalaCirce = (project in file("jwt-scala-circe"))
  .settings(commonSettings *)
  .settings(
    name := "moidc4s-jwt-scala-circe",
    libraryDependencies ++= Seq(
      "com.github.jwt-scala" %% "jwt-circe" % Versions.jwtScala,
    ),
  )
  .dependsOn(
    jwtScalaCore % "compile->compile;test->test",
  )

lazy val it = (project in file("it"))
  .settings(commonSettings *)
  .settings(
    name                     := "integration",
    publish / skip           := true,
    Test / parallelExecution := false,
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-netty-client" % Versions.nettyHttp4s,
    ),
  )
  .dependsOn(
    Seq(
      core,
      zio,
      catsEffect,
      jwtScalaZio,
      jwtScalaCirce,
      circe,
      zioJson,
      zioHttp,
      http4s,
    ).map(_ % "compile->compile;test->test"): _*,
  )
