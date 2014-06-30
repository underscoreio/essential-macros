import java.util.Date

import scala.reflect.macros.blackbox.Context
import scala.language.experimental.macros

object MaximumMacros {

  def maximum(a: Int, b: Int): Int =
    macro maximumMacro

  def maximumMacro(c: Context)(a: c.Tree, b: c.Tree): c.Tree = {
    import c.universe._
    q"if($a > $b) $a else $b"
  }

  def betterMaximum(a: Double, b: Double): Double =
    macro betterMaximumMacro

  def betterMaximumMacro(c: Context)(a: c.Tree, b: c.Tree): c.Tree = {
    import c.universe._

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
     if($temp1 > $temp2) $temp1 else $temp2
     """
  }
}
