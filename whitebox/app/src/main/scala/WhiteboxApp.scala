object WhiteboxApp extends App {
  import WhiteboxMacros._

  // Each macro here is defined with a return type of `Option[Int]`.
  // Each macro actually expands to an expression of type `Some[Int]`.

  val blackboxOption = blackboxOptionOf(2)
  val whiteboxOption = whiteboxOptionOf(2)

  // The whitebox macro expands to an expression of type `Some[Int]`,
  // so this line of code compiles regardless of the type of the variable:

  val whiteboxSome: Some[Int] = whiteboxOption

  // The blackbox macro retains the type `Option[Int]`,
  // so the following code does not compile:

  // val blackboxSome: Some[Int] = blackboxOption

  // --------------------------------------------

  println("It compiles!")
  println("See the source code for an explanation!")
}
