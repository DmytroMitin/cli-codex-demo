class MainSuite extends munit.FunSuite {
  private def runMain(
      args: Seq[String] = Seq.empty,
      props: Map[String, String] = Map.empty,
      env: Map[String, String] = Map.empty,
      stdin: Option[String] = None
  ): String = {
    val javaBin = s"${sys.props("java.home")}/bin/java"
    val scalaBinaryVersion = scala.util.Properties.versionNumberString
      .split('.')
      .take(2)
      .mkString(".")
    val mainClasses = (os.pwd / "target" / s"scala-$scalaBinaryVersion" / "classes").toString
    val scalaVersion = scala.util.Properties.versionNumberString
    val scalaLibraryFromClass = new java.io.File(
      classOf[scala.App].getProtectionDomain.getCodeSource.getLocation.toURI
    ).getPath
    val scalaLibraryCandidates = Seq(
      os.pwd / ".coursier-local" / "v1" / "https" / "repo1.maven.org" / "maven2" / "org" / "scala-lang" / "scala-library" / scalaVersion / s"scala-library-$scalaVersion.jar",
      os.pwd / ".ivy2-local" / "cache" / "org.scala-lang" / "scala-library" / "jars" / s"scala-library-$scalaVersion.jar",
      os.Path(sys.props("user.home")) / ".cache" / "coursier" / "v1" / "https" / "repo1.maven.org" / "maven2" / "org" / "scala-lang" / "scala-library" / scalaVersion / s"scala-library-$scalaVersion.jar",
      os.Path(sys.props("user.home")) / ".ivy2" / "cache" / "org.scala-lang" / "scala-library" / "jars" / s"scala-library-$scalaVersion.jar"
    )
    val scalaLibrary = scalaLibraryCandidates.find(os.exists).map(_.toString).getOrElse(scalaLibraryFromClass)
    val configJar = new java.io.File(
      classOf[com.typesafe.config.ConfigFactory].getProtectionDomain.getCodeSource.getLocation.toURI
    ).getPath
    val classpath = Seq(mainClasses, scalaLibrary, configJar).mkString(java.io.File.pathSeparator)
    val propArgs = props.toSeq.flatMap { case (key, value) => Seq(s"-D${key}=${value}") }
    val cmd = Seq(javaBin, "-cp", classpath) ++ propArgs ++ Seq("Main") ++ args

    val result = scala.util.Using(
      os
        .proc(cmd)
        .spawn(
          cwd = os.pwd,
          env = env,
          stdin = os.Pipe,
          stdout = os.Pipe,
          stderr = os.Pipe,
          mergeErrIntoOut = true
        )
    ) { subProcess =>
      stdin.foreach(subProcess.stdin.write)
      subProcess.stdin.flush()
      subProcess.stdin.close()
      subProcess.waitFor()
      val output = subProcess.stdout.text()
      val exitCode = subProcess.exitCode()
      assertEquals(exitCode, 0, s"process exited with $exitCode and output: $output")
      output.trim
    }

    result.get
  }

  test("CLI arg takes effect") {
    val result = runMain(args = Seq("From CLI"))
    assertEquals(result, "From CLI")
  }

  test("JVM property takes effect") {
    val result = runMain(props = Map("hello.message" -> "From JVM property"))
    assertEquals(result, "From JVM property")
  }

  test("Env var takes effect") {
    val result = runMain(env = Map("HELLO_MESSAGE" -> "From env"))
    assertEquals(result, "From env")
  }

  test("Typesafe config takes effect") {
    val configFile = os.temp(contents = """hello.message = "From config"""")
    val result = runMain(props = Map("config.file" -> configFile.toString))
    assertEquals(result, "From config")
  }

  test("stdin input takes effect") {
    val result = runMain(stdin = Some("From stdin\n"))
    assertEquals(result, "From stdin")
  }
}
