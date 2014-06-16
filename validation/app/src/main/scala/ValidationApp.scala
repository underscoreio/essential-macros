import validatr._

object ValidationApp extends App {

  // Data types:

  case class Person(name: String, age: Int, address: Address)
  case class Address(house: Int, street: String)

  // Validators:

  // `validate[A]` creates a empty validator for objects of type `A`.
  // This validator will never return any errors, regardless of what
  // data is passed to it.

  // The `field` method creates and returns a new validator with
  // all previous rules plus a new rule, as specified in the second
  // set of arguments:
  //
  //  - validate[Foo].field("bar", _.bar)(barValidator)
  //
  //    This form is a method that extracts `foo.bar`, validates it
  //    with `barValidator`, and prefixes any error messages with the
  //    field name "bar"
  //
  //  - validate[Foo].field(_.bar)(barValidator)
  //
  //    This form is a macro that expands to the form above. It
  //    automatically fills in the field name "bar" from the name
  //    of the accessor
  //
  // In either case the `barValidator` method can be provided implicitly.

  implicit val addressValidator: Validator[Address] =
    validate[Address].
    field(_.house)(gte(1)).
    field(_.street)(nonEmpty)

  implicit val personValidator: Validator[Person] =
    validate[Person].
    field(_.name)(nonEmpty).
    field(_.age)(gte(21)).
    field(_.address)

  // Test data:

  val dave       = Person("Dave", 35, Address( 1, "My Street"))
  val badAge     = Person("Dave", -1, Address( 1, "My Street"))
  val badAddress = Person("Dave", 35, Address(-1, ""))
  val badAll     = Person("",     -1, Address(-1, ""))

  println("1. " + (personValidator(dave)       mkString "\n   "))
  println("2. " + (personValidator(badAge)     mkString "\n   "))
  println("3. " + (personValidator(badAddress) mkString "\n   "))
  println("4. " + (personValidator(badAll)     mkString "\n   "))
}
