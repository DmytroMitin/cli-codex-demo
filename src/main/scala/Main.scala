object Main extends App {
  val defaultMessage = "Hello, world!"
  val cliMessage = args.headOption
  val propMessage = sys.props.get("hello.message")
  val envMessage = sys.env.get("HELLO_MESSAGE")
  val configMessage = {
    val shouldCheckConfig = cliMessage.isEmpty && propMessage.isEmpty && envMessage.isEmpty
    if (shouldCheckConfig) {
      try {
        val config = com.typesafe.config.ConfigFactory.load()
        Option
          .when(config.hasPath("hello.message") && config.getValue("hello.message").valueType() == com.typesafe.config.ConfigValueType.STRING)(
            config.getString("hello.message")
          )
          .filter(_.nonEmpty)
      } catch {
        case _: com.typesafe.config.ConfigException => None
      }
    } else None
  }
  val stdinMessage =
    Option.when(cliMessage.isEmpty && propMessage.isEmpty && envMessage.isEmpty && configMessage.isEmpty) {
      Option(scala.io.StdIn.readLine()).filter(_.nonEmpty)
    }.flatten
  val message = cliMessage
    .orElse(propMessage)
    .orElse(envMessage)
    .orElse(configMessage)
    .orElse(stdinMessage)
    .getOrElse(defaultMessage)
  println(message)
}
