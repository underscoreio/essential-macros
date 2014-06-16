object AssertApp extends App {
  import AssertMacros.assert

  val a = 1
  val b = 2

  // This simple version of Scala's `assert` macro prints informative
  // error messages only in the case of a failed assertion of the
  // for `a == b`:

  try {
    assert(a == a)
  } catch {
    case exn: AssertionError =>
      println("1. " + exn.getMessage)
  }

  try {
    assert(a.toString == b.toString)
  } catch {
    case exn: AssertionError =>
      println("2. " + exn.getMessage)
  }

  try {
    assert(true)
  } catch {
    case exn: AssertionError =>
      println("3. " + exn.getMessage)
  }

  try {
    assert(false)
  } catch {
    case exn: AssertionError =>
      println("4. " + exn.getMessage)
  }
}
