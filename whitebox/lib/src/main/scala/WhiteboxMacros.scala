import scala.language.experimental.macros

import scala.reflect.macros.blackbox.{ Context => BlackboxContext }
import scala.reflect.macros.whitebox.{ Context => WhiteboxContext }

object WhiteboxMacros {
  def blackboxOptionOf[A](value: A): Option[A] =
    macro blackboxOptionOfMacro[A]

  def whiteboxOptionOf[A](value: A): Option[A] =
    macro whiteboxOptionOfMacro[A]

  def blackboxOptionOfMacro[A: c.WeakTypeTag](c: BlackboxContext)(value: c.Tree) = {
    import c.universe._
    q"Some($value)"
  }

  def whiteboxOptionOfMacro[A: c.WeakTypeTag](c: WhiteboxContext)(value: c.Tree) = {
    import c.universe._
    q"Some($value)"
  }
}
