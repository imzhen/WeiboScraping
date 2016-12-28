name := "WeiboScraping"

version := "1.0"

scalaVersion := "2.11.8"

val sprayVersion = "1.3+"
val akkaVersion = "2.4+"

libraryDependencies ++= {
  Seq(
    "org.jsoup" % "jsoup" % "1.8+",
    "org.scalaj" %% "scalaj-http" % "2.3.0",
    "io.spray" %% "spray-client" % sprayVersion,
    "io.spray" %% "spray-json" % sprayVersion,
    "com.typesafe.akka" %% "akka-actor" % akkaVersion
  )
}
    