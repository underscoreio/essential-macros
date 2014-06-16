import scala.language.experimental.macros

object Csv extends CsvImplicits {
  def writeCsv[A: CsvFormat](values: Traversable[A]): String = {
    values.map(implicitly[CsvFormat[A]]).map(_.mkString(",")).mkString("\n")
  }
}
