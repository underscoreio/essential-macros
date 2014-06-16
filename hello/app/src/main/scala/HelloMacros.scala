object HelloMacros extends App {
  import LibraryMacros._

  // Greeting is a macro that that prints "Hello world!"
  // and the time at the point of running the macro.
  //
  // In other words, it prints the time at the point of compilation:
  println(greeting)

  // Contrast this with HelloMacros.scala that executes at run time.
}
