import scala.language.experimental.macros

trait LowPriorityCsvImplicits {
  implicit def csvFormat[A]: CsvFormat[A] =
    macro CsvMacros.csvFormatMacro[A]
}

trait CsvImplicits extends LowPriorityCsvImplicits {
  def apply[A](func: A => Seq[String]) = new CsvFormat[A] {
    def apply(value: A) = func(value)
  }

  implicit val stringFormat = apply[String] { value =>
    Seq(value)
  }

  implicit val intFormat = apply[Int] { value =>
    Seq(value.toString)
  }
}
