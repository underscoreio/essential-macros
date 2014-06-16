package validatr

import scala.language.implicitConversions

case class ValidationResult(message: String, path: Seq[String] = Seq.empty, level: ValidationResult.Level = ValidationResult.Level.Error) {
  def prefix(prefix: String) =
    this.copy(path = prefix +: path)

  def prettyPath = path.mkString(".")

  override def toString = level match {
    case ValidationResult.Level.Error   => s"Error($prettyPath : $message)"
    case ValidationResult.Level.Warning => s"Warning($prettyPath : $message)"
  }

}

object ValidationResult {
  sealed trait Level { def id: String ; def toInt: Int }
  object Level {
    final case object Error   extends Level { val id = "error"   ; val toInt = 0 }
    final case object Warning extends Level { val id = "warning" ; val toInt = 1 }
  }
}