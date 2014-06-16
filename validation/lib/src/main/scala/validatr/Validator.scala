package validatr

// Object that validates objects of type `A`:
//
//    validtor.apply(someObject)
//
// Validation returns a list of ValidationResults,
// which contain error messages, field names, and
// error levels ("error" or "warning").
trait Validator[A] {
  import scala.language.experimental.macros

  def apply(data: A): Seq[ValidationResult]

  def prefix(str: String) = Validator { data: A =>
    this(data) map (_ prefix str)
  }

  def contramap[B](func: B => A) = Validator { data: B =>
    this(func(data))
  }

  def and(that: Validator[A]) = Validator { data: A =>
    this(data) ++ that(data)
  }

  def whole(implicit next: Validator[A]) =
    this and next

  def field[B](accessor: A => B)(implicit inner: Validator[B]): Validator[A] =
    macro ValidatorMacros.fieldMacro[A, B]

  def field[B](name: String, accessor: A => B)(implicit inner: Validator[B]): Validator[A] =
    this and (inner contramap accessor prefix name)
}

object Validator {
  import scala.language.implicitConversions

  implicit def apply[A](func: A => Seq[ValidationResult]) = new Validator[A] {
    def apply(data: A) =
      func(data)
  }

  def pass[A] = new Validator[A] {
    def apply(data: A) = Seq.empty
  }
}
