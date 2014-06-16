import java.util.Date

import scala.reflect.macros.blackbox.Context
import scala.language.experimental.macros

object LibraryMacros {
  def greeting: String = macro greetingMacro

  def greetingMacro(c: Context): c.Tree = {
    import c.universe._

    val now = new Date().toString

    q"""
     "Hi! This code was compiled at " +
     $now
     """
  }
}
