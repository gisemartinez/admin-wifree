name := """wifree"""
organization := "com.wifree"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava, PlayEbean, SbtTwirl)

scalaVersion := "2.12.5"

crossScalaVersions := Seq("2.11.12", "2.12.5")

//javacOptions ++= Seq("-Xlint:unchecked")

val playVersion = "2.6.12"
val pac4jVersion = "2.2.1"

// Test Database
libraryDependencies += "com.h2database" % "h2" % "1.4.196"

libraryDependencies ++= Seq(
    guice,
    openId,
    filters,
    ws,
    ehcache,
    "com.typesafe.play" %% "play-json" % "2.6.9",
    "com.typesafe.play" % "play-cache_2.12" % playVersion,
//    "uk.co.panaxiom" %% "play-jongo" % "2.0.0-jongo1.3",
    "org.reactivemongo" % "play2-reactivemongo_2.12" % "0.13.0-play26",
    "org.postgresql" % "postgresql" % "42.0.0",
    "org.webjars" % "bootstrap" % "3.0.0",
    "org.pac4j" %% "play-pac4j" % "5.0.0",
    "org.pac4j" % "pac4j-oauth" % pac4jVersion,
    "org.pac4j" % "pac4j-http" % pac4jVersion,
    "org.pac4j" % "pac4j-oidc" % pac4jVersion,
    "commons-io" % "commons-io" % "2.4",
    "be.objectify" % "deadbolt-java_2.12" % "2.6.3",
    "javax.xml.bind" % "jaxb-api" % "2.3.1",
    "javax.annotation" % "javax.annotation-api" % "1.3.2",
    "javax.el" % "javax.el-api" % "3.0.0",
    "org.glassfish" % "javax.el" % "3.0.0",
    "javax.xml.bind" % "jaxb-api" % "2.3.0",
      javaJdbc % Test,
    "org.assertj" % "assertj-core" % "3.6.2" % Test,
    "org.awaitility" % "awaitility" % "2.0.0" % Test,
    "org.mockito" % "mockito-all" % "1.10.19" % Test
)

// Make verbose tests
testOptions in Test := Seq(Tests.Argument(TestFrameworks.JUnit, "-a", "-v"))

routesGenerator := InjectedRoutesGenerator

//fork := true // required for "sbt run" to pick up javaOptions

//javaOptions += "-Dplay.editor=http://localhost:63342/api/file/?file=%s&line=%s"