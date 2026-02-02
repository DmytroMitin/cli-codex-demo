object Main extends App {
  val defaultMessage = "Hello, world!"
  val message = args.headOption
    .orElse(sys.props.get("hello.message"))
    .orElse(sys.env.get("HELLO_MESSAGE"))
    .getOrElse(defaultMessage)
  println(message)
}
