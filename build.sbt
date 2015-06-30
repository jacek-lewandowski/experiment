import sbt.Keys._

name := "experiment"
version := "1.0"
scalaVersion := "2.11.6"
webInfClasses in webapp := true
resolvers += "vaadin-addons" at "http://maven.vaadin.com/vaadin-addons"

jetty()

libraryDependencies += "javax.servlet" % "javax.servlet-api" % "3.0.1" % "provided"

val VaadinVersion = "7.4.7"
val VaadinDeps = Seq(
  "com.vaadin" % "vaadin-server" % VaadinVersion % "compile",
  "com.vaadin" % "vaadin-client-compiled" % VaadinVersion % "compile",
  "com.vaadin" % "vaadin-client" % VaadinVersion % "compile",
  "com.vaadin" % "vaadin-client-compiler" % VaadinVersion % "compile",
  "com.vaadin" % "vaadin-push" % VaadinVersion % "compile",
  "com.vaadin" % "vaadin-themes" % VaadinVersion % "compile",

  "org.vaadin.addons" % "rinne" % "0.2.0" % "compile",
  "org.vaadin.addons" % "flexibleoptiongroup" % "2.3.0" % "compile"
).map(_ excludeAll ExclusionRule(organization = "org.slf4j"))

val SparkCassandraConnectorDeps = Seq(
  "com.datastax.spark" %% "spark-cassandra-connector" % "1.2.3" % "compile" excludeAll ExclusionRule(organization = "org.slf4j"),
  "org.apache.spark" %% "spark-core" % "1.2.2" % "compile" intransitive() excludeAll ExclusionRule(organization = "org.slf4j")
)

val LoggingDeps = Seq(
  "org.slf4j" % "slf4j-api" % "1.7.12" % "compile" force(),
  "org.slf4j" % "slf4j-simple" % "1.7.12" % "compile" force(),
  "ch.qos.logback" % "logback-classic" % "1.1.3"
)

val ScalaDeps = Seq(
  "org.scala-lang" % "scala-reflect" % "2.11.6"
)

val ArtimaDeps = Seq(
  "org.scalatest" %% "scalatest" % "2.2.5" % "test",
  "org.scalactic" %% "scalactic" % "2.2.5" % "compile"
)

val json4sNative = Seq(
  "org.json4s" %% "json4s-native" % "3.2.10" % "compile",
  "org.json4s" %% "json4s-ext" % "3.2.10" % "compile"
)

libraryDependencies ++= VaadinDeps ++ SparkCassandraConnectorDeps ++ LoggingDeps ++ ScalaDeps ++ ArtimaDeps ++ json4sNative

cleanKeepFiles ++= Seq("resolution-cache", "streams", "spark-archives", "classes/VAADIN").map(target.value / _)
updateOptions := updateOptions.value.withCachedResolution(cachedResoluton = true)

javaOptions in container ++= Seq(
  "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 -Dlogback.configuration=logback.xml"
)

