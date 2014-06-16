object AssertApp extends App {
  import AssertMacros.assert

  // This improved version of the macro in the `simpleassert` project
  // prints informative error messages for a variety of basic Scala
  // expressions.
  //
  // The macro works by recursively breaking the argument expression
  // down into sub-expressions of the form `a`, `a.b`, and `a.b(c, ...)`,
  // creating a print statement at each level of recursion.
  //
  // Temporary variables are introduced at each level of recursion
  // to prevent double-evaluation of any part of the target expression.

  // Assertions on fields and methods:

  val a = 1
  def b = 2

  try {
    assert(a == b)
  } catch {
    case exn: Throwable => println(exn)
  }

  // Assertions on local variables and functions:

  try {
    val a = 1
    def b = 2

    assert(a == b)
  } catch {
    case exn: Throwable => println(exn)
  }

  // Assertions on arguments to methods:

  def method2(a: Int, b: Int) = {
    assert(a == b)
  }

  try {
    method2(1, 2)
  } catch {
    case exn: Throwable => println(exn)
  }

  // Assertions on complex expressions:

  try {
    object a { val b = 1 }
    object c { def d(x: Int, y: Int) = x + y }
    val e = 1
    val f = 1

    assert(a.b == c.d(e, f))
  } catch {
    case exn: Throwable => println(exn)
  }


}
