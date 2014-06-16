import scala.language.experimental.macros

import scala.math.Ordering
import scala.reflect.macros.blackbox.Context

// Simple macro-enriched library for sorting case class instances
// based on field names supplied at run-time.
//
// The library revolves around a type `Orderings[A]` that transforms
// field names to instances of `Ordering[A]`. A macro `orderings[A]`
// can be used to auto-generate instances of `Orderings[A]` for case
// classes based on a rudimentary inspection of the fields of the
// case class.


// An annotation that can be placed on a field in a case class
// constructor to define a default ordering:
class defaultOrdering extends scala.annotation.StaticAnnotation

// Trait specifying a mapping from field names to instances of
// `Ordering[A]`:
trait Orderings[A] {
  def apply(field: String, asc: Boolean = true): Ordering[A]
}

object OrderingsMacros {
  def orderings[A]: Orderings[A] =
    macro OrderingsMacros.orderingsMacro[A]
}

class OrderingsMacros(val c: Context) {
  import c.universe._

  // Given a case class definition `case class Foo(a: A, b: B, c: C)`,
  // this macro expands to code as follows:
  //
  //     new Ordering[Foo] {
  //       def apply(field: String, asc: Boolean) = field match {
  //         case "a" => // ...
  //         case "a" => // ...
  //         case "a" => // ...
  //         case _   => // ...
  //       }
  //     }
  //
  // If `A` represents an empty case class or something other than
  // a case class, fail with a suitable compilation error.
  def orderingsMacro[A: c.WeakTypeTag] = {
    val tpe = weakTypeOf[A]

    // Build a `List[MethodSymbol]` storing the declarations
    // of accessor methods on the case class:
    val methods = tpe.decls.toList collect {
      case method: MethodSymbol if method.isCaseAccessor =>
        method
    }

    // If `methods` comes up empty we raise a compilation error:
    if(methods.length < 1) {
      c.abort(c.enclosingPosition, "Type parameter must be a case class with one or more fields")
    }

    // Helper method to generate a case clause given a pattern
    // and an accessor method. We use this twice below: once to
    // generate clauses for each field name, and one to generate
    // a clause for a default case:
    def createCaseClause(pattern: Tree, method: MethodSymbol) = {
      val ret = method.returnType
      cq"$pattern => Ordering.by[$tpe, $ret](_.${method.name})"
    }

    // Generate case clauses for each field in the case class
    // constructor:
    val cases = methods map { method =>
      createCaseClause(pq"${method.name.toString}", method)
    }

    // Helper method to identify an accessor method for
    // a field that has been annotated with `@defaultOrdering`.
    //
    // Annotations on case class constructor arguments are
    // actually attached to private field declarations, not
    // accessor declarations. We use `method.accessed` to
    // get to the field from the method and inspect the
    // annotations there:
    def isAnnotatedDefault(method: MethodSymbol) =
      method.accessed.annotations exists { annote =>
        annote.tree.tpe =:= typeOf[defaultOrdering]
      }

    // Generate a wildcard case clause for a default ordering.
    // If the user has specified a `@defaultOrdering` we use that.
    // Otherwise we use the first field on the case class:
    val default = createCaseClause(
      pq"_",
      methods find isAnnotatedDefault getOrElse methods.head)

    // Finally assemble the various case clauses into a match expression:
    val result =
      q"""
      new Orderings[$tpe] {
        def apply(name: String, asc: Boolean = true) = {
          val base = name match { case ..$cases case $default }
          if(asc) base else base.reverse
        }
      }
      """

    // println(showCode(result))

    result
  }
}
