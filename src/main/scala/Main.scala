object Main extends App {
  val message = sys.env.getOrElse("HELLO_MESSAGE", "Hello, world!")
  println(message)
}
