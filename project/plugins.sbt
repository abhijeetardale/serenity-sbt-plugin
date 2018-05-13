resolvers += "Artima Maven Repository" at "http://repo.artima.com/releases"


addSbtPlugin("com.artima.supersafe" % "sbtplugin" % "1.1.3")
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.5.10")

logLevel := Level.Warn
