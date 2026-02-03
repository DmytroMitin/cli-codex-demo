ThisBuild / scalaVersion := "2.13.12"

lazy val root = (project in file("."))
  .settings(
    name := "hello-world",
    libraryDependencies += "org.scalameta" %% "munit" % "1.0.0" % Test,
    testFrameworks += new TestFramework("munit.Framework")
  )
