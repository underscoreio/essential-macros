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

    q"""
     val temp1 = $a
     val temp2 = $b
     if(temp1 > temp2) temp1 else temp2
     """
  }
}
