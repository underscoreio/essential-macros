package validatr

trait Validators {

  lazy val nonEmpty = validator[String] { str =>
    if(str.length == 0) fail("Must not be empty") else pass
  }

  def gte(min: Int) = validator[Int] { num =>
    if(num < min) fail(s"Must be >= $min") else pass
  }

}