import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

object AssertMacros {
  // This version of `assert` takes a single expression of type `Boolean`,
  // and expands into an `if` statement containing a `throw`.
  //
  // The macro identifies expressions of the form `a == b` and prints
  // an informative error message. It passes other expressions through.

  def assert(expr: Boolean): Unit =
    macro assertMacro

  def assertMacro(c: Context)(expr: c.Tree) = {
    import c.universe._

    expr match {
      // We use quasiquotes to match on the type of expression in which we are interested:

      case q"$a == $b" =>
        // We ask scalac to generate fresh names to avoid potential naming conflicts:
        //
        // See the following (step 10 onwards) for a discussion of name generation:
        //     https://github.com/scalamacros/macrology201/commits/part1
        //
        // Note that this will fail for certain exotic types of arguments
        // due to an owner chain corruption issue. See steps 19 to 23 above
        // for a detailed explanation and workaround.
        val temp1 = c.freshName(TermName("temp"))
        val temp2 = c.freshName(TermName("temp"))

        q"""
        val $temp1 = $a
        val $temp2 = $b
        if($temp1 != $temp2) {
          throw new AssertionError($temp1 + " != " + $temp2)
        }
        """

      // Other forms are passed through:

      case other =>
        q"""
        if(!$other) {
          throw new AssertionError("assertion failed")
        }
        """
    }
  }
}
