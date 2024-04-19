scalaVersion := "2.12.8"
enablePlugins(JavaAppPackaging)
val akkaVersion = "2.6.18"
val akkaHttpVersion = "10.2.8"
val scalaTestVersion = "3.0.5"
name := "Challenge2"
resolvers += "Akka library repository".at("https://repo.akka.io/maven")
libraryDependencies ++= Seq(

  "com.typesafe.akka" %% "akka-http" % "10.2.8",
  "com.typesafe.akka" %% "akka-actor" % "2.6.18",
  "com.typesafe.akka" %% "akka-stream" % "2.6.18",
  "com.datastax.oss"  %  "java-driver-core"           % "4.13.0",   // See https://github.com/akka/alpakka/issues/2556
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
  "ch.qos.logback" % "logback-classic" % "1.2.10",
  "org.mindrot" % "jbcrypt" % "0.3m",
  "com.pauldijou" %% "jwt-spray-json" % "2.1.0",
  "ch.megard" %% "akka-http-cors" % "1.1.3",
  "org.scala-lang.modules" %% "scala-collection-compat" % "2.5.0",
  "com.datastax.spark" % "spark-cassandra-connector_2.12" % "3.2.0",
  "org.apache.spark" %% "spark-core" % "3.3.2",
  "org.apache.spark" %% "spark-sql" % "3.3.2",
  "joda-time" % "joda-time" % "2.9.3",
  "org.joda" % "joda-convert" % "1.8",
  "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion,
  // testing
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
  "org.scalatest" %% "scalatest" % scalaTestVersion,

)