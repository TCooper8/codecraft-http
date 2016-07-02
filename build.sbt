name := "http"

organization := "codecraft"

version := "1.0.0-SNAPSHOT"

scalaVersion := "2.11.8"

enablePlugins(_root_.sbtdocker.DockerPlugin)

lazy val root = (project in file(".")).enablePlugins(PlayScala)

libraryDependencies ++= Seq(
  cache,
  ws,
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test,
  "codecraft" %% "cloud" % "1.0.0-SNAPSHOT",
  "codecraft" %% "user-messages" % "1.0.0-SNAPSHOT",
  "codecraft" %% "auth-messages" % "1.0.0-SNAPSHOT"
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

dockerfile in docker := {
	val jarFile: File = sbt.Keys.`package`.in(Compile, packageBin).value
  val classpath = (managedClasspath in Compile).value
  val mainclass = mainClass.in(Compile, packageBin).value.getOrElse(sys.error("Expected exactly one main class"))
  val jarTarget = s"/app/${jarFile.getName}"
  // Make a colon separated classpath with the JAR file
  val classpathString = classpath.files.map("/app/" + _.getName)
    .mkString(":") + ":" + jarTarget
  new Dockerfile {
    // Base image
    //from("alpine:3.2")
    from("anapsix/alpine-java")
    //run("apk --update add openjdk7-jre")
    //run("apk", "--update", "add", "openjdk7-jre")
    // Add all files on the classpath
    add(classpath.files, "/app/")
    // Add the JAR file
    add(jarFile, jarTarget)
    // On launch run Java with the classpath and the main class
    entryPoint("java", "-cp", classpathString, mainclass)
  }
}

