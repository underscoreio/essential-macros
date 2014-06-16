import java.util.Date

import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context
import scala.util.matching._

object PrintTree {
  def printTree(title: String)(expr: Any): Unit =
    macro printTreeMacro

  def printTreeMacro(c: Context)(title: c.Tree)(expr: c.Tree) = {
    import c.universe._
    import PrettyPrint._

    // `showCode` and `showRaw` are useful methods from the macro API
    // that convert Trees to Strings:
    //
    //  - `showCode` pretty-prints the expression in the tree in a
    //    form that could be passed to an `eval` method
    //
    //  - `showRaw` renders a case-class-like printout of the tree
    //
    // `prettify` is a rudimentary pretty-printer for case-class-like,
    // expressions built specifically for this macro. It does not work
    // in all cases:

    val code: String = showCode(expr)
    val tree: String = prettify(showRaw(expr))

    // Rather than print the values of `code` and `tree` directly, we
    // inject the Strings in `code` and `tree` into a `println` statement.
    //
    // This allows us to interleave calls to `printTree` in our application
    // with regular debugging statements such as `println`:

    q"""
    println(
      $title.toUpperCase + "\n\n" +
      "Desugared code:\n"  + $code + "\n\n" +
      "Underlying tree:\n" + $tree + "\n\n"
    )
    """
  }
}
