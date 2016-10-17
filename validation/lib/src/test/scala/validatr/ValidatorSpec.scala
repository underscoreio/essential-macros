package validatr

import org.specs2.mutable._

class ValidatorSpec extends Specification {

  case class Person(name: String, age: Int, address: Address)
  case class Address(house: Int, street: String)

  val dave       = Person("Dave", 35, Address( 1, "My Street"))
  val badAge     = Person("Dave", -1, Address( 1, "My Street"))
  val badAddress = Person("Dave", 35, Address(-1, ""))
  val badAll     = Person("",     -1, Address(-1, ""))

  implicit val addressValidator: Validator[Address] =
    validate[Address].
    field(_.house)(gte(1)).
    field(_.street)(nonEmpty)

  implicit val personValidator: Validator[Person] =
    validate[Person].
    field(_.name)(nonEmpty).
    field(_.age)(gte(21)).
    field(_.address)

  "Validator(data => seq)" >> {
    nonEmpty("") mustEqual fail("Must not be empty")

    nonEmpty("foo") mustEqual Nil
  }

  "validator.prefix" >> {
    (nonEmpty prefix "field" apply "") mustEqual fail("Must not be empty", "field" :: Nil)
  }

  "validatr.validate" >> {
    (validate[Person] apply dave) mustEqual Nil
  }

  "validatorBuilder.field" >> {
    val func = (str: String) => pass

    personValidator(dave) mustEqual Nil

    personValidator(badAddress) mustEqual {
      fail("Must be >= 1", "address" :: "house" :: Nil) ++
      fail("Must not be empty", "address" :: "street" :: Nil)
    }

    personValidator(badAge) mustEqual fail("Must be >= 21", "age" :: Nil)

    personValidator(badAll) mustEqual {
      fail("Must not be empty", "name" :: Nil) ++
      fail("Must be >= 21", "age" :: Nil) ++
      fail("Must be >= 1", "address" :: "house" :: Nil) ++
      fail("Must not be empty", "address" :: "street" :: Nil)
    }
  }
}
