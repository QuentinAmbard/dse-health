import play.PlayImport.PlayKeys.playRunHooks

name := """play-react-webpack"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

//scalaVersion := "2.12.2"
scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  specs2 % Test,
  "com.codeborne" % "phantomjsdriver" % "1.2.1",
  "com.google.inject.extensions" % "guice-multibindings" % "4.0",
  "org.apache.commons" % "commons-compress" % "1.14",
  "io.thekraken" % "grok" % "0.1.5",
  "com.datastax.cassandra"  % "cassandra-driver-core" % "3.2.0",
  "com.datastax.cassandra"  % "cassandra-driver-mapping" % "3.2.0",
  "com.eaio.uuid" % "uuid" % "3.2",
  "com.fasterxml.jackson.core" % "jackson-core" % "2.5.4",
  "com.fasterxml.jackson.core" % "jackson-annotations" % "2.5.4",
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.5.4",
  "com.fasterxml.jackson.dataformat" % "jackson-dataformat-yaml" % "2.5.4",
  "org.scalactic" %% "scalactic" % "3.0.1",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test",
  "org.scalamock" %% "scalamock-scalatest-support" % "3.5.0" % Test

)

playRunHooks <+= baseDirectory.map(Webpack.apply)

routesGenerator := InjectedRoutesGenerator

excludeFilter in (Assets, JshintKeys.jshint) := "*.js"

watchSources ~= { (ws: Seq[File]) =>
  ws filterNot { path =>
    path.getName.endsWith(".js") || path.getName == ("build")
  }
}

pipelineStages := Seq(digest, gzip)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"


