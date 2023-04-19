sbtPlugin := true

organization := "net.serenitybdd.plugins.sbt"

name := "SerenitySbtPlugin"

version := "1.1.0-SNAPSHOT"

scalacOptions ++= Seq("-deprecation", "-feature")

resolvers += Resolver.sonatypeRepo("snapshots")

val serenityVersion = "3.6.23"

libraryDependencies ++= Seq(
//  "junit" % "junit" % "4.11",
  "com.novocode" % "junit-interface" % "0.11",
  "net.serenity-bdd" % "serenity-core" % serenityVersion,
  "net.serenity-bdd" % "serenity-cucumber" % serenityVersion,
  "net.serenity-bdd" % "serenity-junit" % serenityVersion,
  "net.serenity-bdd" % "serenity-jira-plugin" % "3.6.23",
  "org.scalatest" %% "scalatest" % "3.2.15",
  "org.slf4j" % "slf4j-simple" % "2.0.7"
)

publishMavenStyle := false
