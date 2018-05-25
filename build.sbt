sbtPlugin := true

organization := "net.serenitybdd.plugins.sbt"

name := "SerenitySbtPlugin"

version := "1.0.0-SNAPSHOT"

scalacOptions ++= Seq("-deprecation", "-feature")

resolvers += Resolver.sonatypeRepo("snapshots")

libraryDependencies ++= Seq(
  "junit" % "junit" % "4.11",
  "com.novocode" % "junit-interface" % "0.11",
  "org.scala-lang" % "scala-library" % "2.11.11",
  "org.scala-lang" % "scala-library" % "2.11.11",
  "net.serenity-bdd" % "serenity-core" % "1.9.12",
  "net.serenity-bdd" % "serenity-cucumber" % "1.9.5",
  "net.serenity-bdd" % "serenity-junit" % "1.9.12",
  "net.serenity-bdd" % "serenity-jira-plugin" % "1.1.1",
  "org.scalatest" % "scalatest_2.12" % "3.0.5",
  "org.slf4j" % "slf4j-simple" % "1.7.21"
)

publishMavenStyle := false

