package object validatr extends Validators {

  // Creates an empty validator for objects of type `A`.
  // Users can build validators with more rules in them using
  // the `field` method/macro on `ValidatorBuilder`:

  def validate[A] = Validator.pass[A]

  def pass: Seq[ValidationResult] =
    Seq.empty

  def fail(message: String, path: Seq[String] = Seq.empty): Seq[ValidationResult] =
    Seq(ValidationResult(message, path, ValidationResult.Level.Error))

  def warn(message: String, path: Seq[String] = Seq.empty): Seq[ValidationResult] =
    Seq(ValidationResult(message, path, ValidationResult.Level.Warning))

  def validator[A](func: A => Seq[ValidationResult]) = new Validator[A] {
    def apply(value: A) = func(value)
  }
}