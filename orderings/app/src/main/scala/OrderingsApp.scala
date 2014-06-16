object OrderingsApp extends App {
  import OrderingsMacros._

  case class Person(
    name: String,
    @defaultOrdering age: Int)

  val people = List(
    Person("Anne",    35),
    Person("Bob",     45),
    Person("Charlie", 20))

  // The `orderings` macro expands to an instance of `Orderings[A]`,
  // which is effectively a function from a string to an `Ordering[A]`.
  //
  // The implementation assumes `A` is a case class, and allows
  // ascending or descending ordering by any of the fields in the
  // primary constructor.
  //
  // The `@defaultOrdering` annotation above marks which ordering
  // to return when the user supplies an invalid field name. This isn't
  // great library design, but it demonstrates compile-time inspection
  // of annotations using macros.

  val by = orderings[Person]

  println("1a. " + people.sorted(by("name")))
  println("1b. " + people.sorted(by("name", false)))
  println("2a. " + people.sorted(by("age")))
  println("2b. " + people.sorted(by("age", false)))
  println("3a. " + people.sorted(by("whatever")))
  println("3b. " + people.sorted(by("whatever", false)))
}
