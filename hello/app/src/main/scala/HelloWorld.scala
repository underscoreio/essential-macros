object HelloWorld extends App {
  import LibraryMethods._

  // In this implementation, greeting is a method
  // that prints "Hello world!" and the time at the point of running the method:
  //
  // In other words, it prints the time at the point of execution:
  println(greeting)

  // Contrast this with HelloMacros.scala that executes at compile time.
}
