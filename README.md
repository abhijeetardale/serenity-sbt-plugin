# SBT RSS

This is an SBT 0.13 plugin which uses AutoPlugin and the ROME libraries to 
query websites for RSS feeds and display the titles inside SBT.

This is a fairly simple SBT plugin, and has lots of comments to show how you can add a command to SBT.

## Building 

You can build and publish the plugin in the normal way to your local Ivy repository:

```
sbt publish-local
```

## Installation

You must first download the git project and build it.  It is not available in the maven repository.

In `project/plugins.sbt`:

```
addSbtPlugin("com.typesafe.sbt" % "sbt-rss" % "1.0.0-SNAPSHOT")
```

In `build.sbt`:

```
val myProject = (project in file(".")).enablePlugins(SbtRss)

```

## Usage



