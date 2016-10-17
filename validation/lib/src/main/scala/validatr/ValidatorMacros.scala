package validatr

import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

class ValidatorMacros(val c: Context) {
  import c.universe._

  def fieldMacro[A: c.WeakTypeTag, B: c.WeakTypeTag](accessor: c.Tree)(inner: c.Tree) = {
    val name = accessor match {
      case q"($param) => $obj.$name" =>
        name

      case other =>
        c.abort(c.enclosingPosition, errorMessage(s"Argument is not an accessor function literal."))
    }

    q"${c.prefix}.field(${name.toString}, $accessor)($inner)"
  }

  def errorMessage(prefix: String) =
    s"""
     |$prefix
     |
     |The argument must be a function literal of the form `fieldName => expression`,
     |where `fieldName` is the name of a field in the object being validated.
     |
     |Alternatively use the `field(fieldName, accessor)(validationFunction)` method,
     |which allows you to specify the field name manually.
     """.stripMargin
}
