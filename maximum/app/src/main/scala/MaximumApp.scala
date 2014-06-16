object MaximumApp extends App {
  import MaximumMacros._

  // Use-cases with purely functional non-side-effecting code:

  val x = 1
  var y = 2

  println("maximum(x, y): " + maximum(x, y))

  println("maximum(x + 10, y * 3): " + maximum(x + 10, y * 3))

  // Use-cases with side-effecting code:
  //
  // The expanded form of `maximum()` contains two references
  // to each argument, so we get the wrong result.
  //
  // By contrast, the expanded form of `betterMaximum()`
  // uses temporary variables to ensure each argument is only
  // executed once.

  def incY = {
    val ans = y
    y = y + 1
    ans
  }

  println("maximum(x, y++): " + maximum(x, incY))

  println("maximum(x, y++): " + betterMaximum(x, incY))

  // Uncomment this code to see that temp1 and temp2 are embedded in a block.
  // Their definitions are not leaked into the surrounding context, so this code will not compile:
  // betterMaximum(10, 20)
  // println(temp1)
}
