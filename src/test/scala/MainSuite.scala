import java.nio.charset.StandardCharsets

class MainSuite extends munit.FunSuite {
  private def runMain(
      args: Seq[String] = Seq.empty,
      props: Map[String, String] = Map.empty,
      env: Map[String, String] = Map.empty,
      stdin: Option[String] = None
  ): String = {
    val javaBin = s"${System.getProperty("java.home")}/bin/java"
    val classpath = System.getProperty("java.class.path")
    val propArgs = props.toSeq.flatMap { case (key, value) => Seq(s"-D${key}=${value}") }
    val cmd = Seq(javaBin, "-cp", classpath) ++ propArgs ++ Seq("Main") ++ args

    val pb = new ProcessBuilder(cmd: _*)
    pb.redirectErrorStream(true)
    val pbEnv = pb.environment()
    env.foreach { case (key, value) => pbEnv.put(key, value) }

    val process = pb.start()
    stdin match {
      case Some(input) =>
        val os = process.getOutputStream
        os.write(input.getBytes(StandardCharsets.UTF_8))
        os.flush()
        os.close()
      case None =>
        process.getOutputStream.close()
    }

    val output = new String(process.getInputStream.readAllBytes(), StandardCharsets.UTF_8)
    val exitCode = process.waitFor()
    assertEquals(exitCode, 0, s"process exited with $exitCode and output: $output")
    output.trim
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

  test("stdin input takes effect") {
    val result = runMain(stdin = Some("From stdin\n"))
    assertEquals(result, "From stdin")
  }
}
