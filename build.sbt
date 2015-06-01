name := """Einkaufsliste"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  javaJpa,
  "mysql" % "mysql-connector-java" % "5.1.34",
  "org.hibernate" % "hibernate-entitymanager" % "3.6.10.Final",
  javaWs,
  "postgresql" % "postgresql" % "9.1-901-1.jdbc4",
  "org.whispersystems" % "gcm-sender-async" % "0.1.3"  
)
