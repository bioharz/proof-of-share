name := "proof-of-share"
organization := "at.shokri"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava, PlayEbean)

scalaVersion := "2.12.4"

libraryDependencies += guice
libraryDependencies += javaJdbc
libraryDependencies += javaJpa
libraryDependencies += "com.h2database" % "h2" % "1.4.197"

//TODO: experimental!!! see: https://adrianhurt.github.io/play-bootstrap/
// Resolver is needed only for SNAPSHOT versions
resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"
libraryDependencies ++= Seq(
  "com.adrianhurt" %% "play-bootstrap" % "1.4-P26-B4-SNAPSHOT"
)