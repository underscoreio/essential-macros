import java.util.Date

object LibraryMethods {
  def greeting: String = {
    val now = new Date().toString

    "Hi! The current time is " +
    now
  }
}
