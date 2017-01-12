name := "WeiboScraping"

version := "1.0"

scalaVersion := "2.11.8"

val akkaVersion = "2.4+"
val akkaHttpVersion = "10.0.1"

libraryDependencies ++= {
  Seq(
    "org.scalaj" %% "scalaj-http" % "2.3.0",
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    "com.typesafe.akka" %% "akka-http-core" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-actor" % akkaVersion
  )
}
    