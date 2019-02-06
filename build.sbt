lazy val releaseVersion = "0.4"

lazy val apriori4j = (project in file("apriori4j")).settings(
  name                := "apriori4j",
  libraryDependencies ++= Seq(
    "org.slf4j"          % "slf4j-api"       % "1.7.12",
    "ch.qos.logback"     % "logback-classic" % "1.1.3"   % Test,
    "org.apache.commons" % "commons-io"      % "1.3.2"   % Test,
    "junit"              % "junit"           % "4.12"    % Test,
    "org.hamcrest"       % "hamcrest-all"    % "1.3"     % Test,
    "com.novocode"       % "junit-interface" % "0.11"    % Test
  ),
  crossPaths          := false,
  autoScalaLibrary    := false
).settings(commonSettings: _*)
 .settings(publishSettings: _*)

lazy val apriori4s = (project in file("apriori4s")).settings(
  name                := "apriori4s",
  scalaVersion        := "2.12.8",
  crossScalaVersions  := Seq("2.10.7", "2.11.12", "2.12.8"),
  libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % Test,
  logBuffered in Test := false,
  scalacOptions       ++= Seq("-unchecked", "-deprecation", "-feature")
).settings(commonSettings: _*)
 .settings(publishSettings: _*)
 .dependsOn(apriori4j)

lazy val commonSettings = Seq(
  organization := "com.github.seratch",
  version := releaseVersion,
  javacOptions ++= Seq("-source", "1.7", "-target", "1.7", "-encoding", "UTF-8", "-Xlint:-options", "-Xlint:unchecked"),
  javacOptions in doc := Seq("-source", "1.7"),
  transitiveClassifiers in Global := Seq(Artifact.SourceClassifier)
)

lazy val publishSettings = Seq(
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (version.value.trim.endsWith("SNAPSHOT")) Some("snapshots" at nexus + "content/repositories/snapshots")
    else Some("releases" at nexus + "service/local/staging/deploy/maven2")
  },
  publishMavenStyle := true,
  publishArtifact in Test := false,
  pomIncludeRepository := { x => false },
  pomExtra := <url>https://github.com/serach/apriori4j/</url>
    <licenses>
    <license>
      <name>MIT License</name>
      <url>http://www.opensource.org/licenses/mit-license.php</url>
      <distribution>repo</distribution>
    </license>
    </licenses>
    <scm>
      <url>git@github.com:seratch/apriori4j.git</url>
      <connection>scm:git:git@github.com:seratch/apriori4j.git</connection>
    </scm>
    <developers>
      <developer>
        <id>seratch</id>
        <name>Kazuhiro Sera</name>
        <url>http://git.io/sera</url>
      </developer>
    </developers>
)

