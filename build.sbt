name := """codecraft-user-api"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  cache,
  ws,
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test,
  "codecraft" %% "cloud" % "1.0.0-SNAPSHOT",
  "codecraft" %% "user-messages" % "1.0.0-SNAPSHOT",
  "codecraft" %% "music-messages" % "1.0.0-SNAPSHOT"
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
