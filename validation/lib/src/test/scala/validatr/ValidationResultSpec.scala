package validatr

import org.specs2.mutable._

class ValidationResultSpec extends Specification {

  case class Person(name: String, age: Int)

  "validationResult.prefix" >> {
    ValidationResult("Must not be empty.", path = "bar" :: Nil).prefix("foo") mustEqual
      ValidationResult("Must not be empty.", path = "foo" :: "bar" :: Nil)
  }

}