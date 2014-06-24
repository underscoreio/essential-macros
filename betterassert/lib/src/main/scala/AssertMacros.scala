import scala.language.experimental.macros

import scala.collection.mutable
import scala.reflect.macros.blackbox.Context

object AssertMacros {
  def assert(expr: Boolean): Unit =
    macro AssertMacros.assertMacro
}

class AssertMacros(val c: Context) {
  import c.universe._

  /*
  Variant of PreDef.assert that prints the assertion expression
  and the values of intermediate calculations.

  To avoid side-effects, a number of temporary variables
  are introduced to cache the values of printed expressions.

  For example, a tree like this:

    assert(a.b == c.d)

  is transformed into code like this:

    val temp1 = a
    val temp2 = temp1.b
    val temp3 = c
    val temp4 = temp3.d
    val temp5 = temp2 == temp4

    if(!temp5) {
      val printedCode = "a.b == c.d"
      val printedValues = List(
        "a = " + temp1,
        "a.b = " + temp2,
        "c = " + temp3,
        "c.d = " + temp4,
        "a.b == c.d = " + temp5
      )
      throw new AssertionError(
        "Assertion failed: " +
        printedCode +
        printedValues.mkString("\n"))
    }
  */

  // Helper class used to cache information about temporary
  // variables introduced during expansion:
  //
  //  - `original` is the original expression fragment,
  //    the value of which is stored in the temporary variable.
  //
  //    Although `original` is not embedded directly anywhere
  //    in the final code, its value is stored so its printed
  //    form can be included in error messages from failed
  //    assertions.
  //
  //    Example: `a.b(c.d, e.f, g.h)`
  //
  //  - `transformed` is the transformed expression used to
  //    actually calculate the value stored in the variable.
  //
  //    This includes references to temporary variables storing
  //    values from sub-expressions from `original`.
  //
  //    Example: `temp1.b(temp2, temp3, temp4)`
  case class TempVar(original: c.Tree, transformed: c.Tree) {
    // The name of the temporary variable.
    //
    // See the following (step 10 onwards) for a discussion of name generation:
    //     https://github.com/scalamacros/macrology201
    val name  = c.freshName(TermName("temp"))

    // An expression that references this variable,
    // used below to create the `transformed` form of
    // parent expresions:
    val ident = q"$name"

    // An statement that declares this variable:
    val decl  = q"val $name = $transformed"

    // An expression that renders this variable as a string,
    // used to generate error messages for failed assertions:
    val debug = q""" ${showCode(original)} + " = " + $ident """
  }

  // The actual implementation of the assert macro:

  def assertMacro(expr: c.Tree): c.Tree = {
    // We create a mutable buffer of temporary variables
    // and recursively transform `expr`, caching the temporary
    // variables allocated as we go:
    val tempVars = new mutable.ArrayBuffer[TempVar]()

    // Helper method to create and store a temporary variable:
    def tempVar(original: c.Tree, transformed: c.Tree) = {
      val tempVar = TempVar(original, transformed)
      tempVars += tempVar
      tempVar
    }

    // Recursive method that transforms an expression, extracts
    // printable sub-expressions, and caches them in `tempVars`.
    //
    // The return value is the cached temporary variable:
    def transform(tree: c.Tree): c.Tree = tree match {
      // Method application:
      case q"$recv.$method(..$args)" =>
        val newRecv = transform(recv)
        val newArgs = args.map(transform)
        tempVar(tree, q"$newRecv.$method(..$newArgs)").ident

      // Field selection:
      case q"$recv.$field" =>
        val newRecv = transform(recv)
        tempVar(tree, q"$newRecv.$field").ident

      // Plain identifier:
      case ident: Ident =>
        tempVar(ident, ident).ident

      // Anything else is ignored:
      case other =>
        other
    }

    // Transform the whole of `expr`, storing a number of
    // temporary variables as a side-effect:
    val transformed =
      transform(expr)

    // Once all temporary variables have been calculated,
    // we can generate the final assertion code using the
    // contents of `tempVars`:
    val result =
      q"""
      ..${tempVars.map(_.decl)}
      if( ! $transformed ) {
        val printedCode = ${show(expr)}
        val printedValues = List(..${tempVars.map(_.debug)})
        throw new AssertionError(
          "Assertion failed: " +
          printedCode +
          printedValues.mkString("\n  ", "\n  ", "")
        )
      }
      """

    // println(show(result))

    result
  }

}
