case class Person(val name: String, var age: Int)

object PrintTypeApp extends App {
  import PrintType._

  // See `PrintTypeMacros.scala` for a description
  // of these macros.

  type T = List[Int]

  // Print information about a fully known type:

  println("1. List[Int] ====================")

  printStructure[List[Int]]
  printSymbol[List[Int]]
  printDecls[List[Int]]

  // Print information about an aliased fully known type:

  println("2. T ====================")

  printStructure[T]
  printSymbol[T]
  printDecls[T]

  // Print information about a case class companion:

  println("3. Person ====================")

  printSymbol(Person)

  // Print information about an unknown type:

  def genericMethod[A] = {
    println("4. A ====================")

    printStructure[A]
    printSymbol[A]
    printDecls[A]
  }

  // The macros above are expanded when the method
  // definition is compiled. Even though we're binding
  // `A` to `List[Int]` here, the macros will only know
  // rudimentary information about `A`:
  genericMethod[List[Int]]
}
