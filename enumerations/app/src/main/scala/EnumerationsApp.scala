object EnumerationsApp extends App {
  import EnumerationMacros._

  object WeekDay {
    sealed abstract class EnumVal(val id: Int, val name: String) extends Ordered[EnumVal] {
      def compare(that: EnumVal) = this.id - that.id
    }
    case object Mon extends EnumVal(0, "Mon")
    case object Tue extends EnumVal(1, "Tue")
    case object Wed extends EnumVal(2, "Wed")
    case object Thu extends EnumVal(3, "Thu")
    case object Fri extends EnumVal(4, "Fri")
    case object Sat extends EnumVal(5, "Sat")
    case object Sun extends EnumVal(6, "Sun")

    val values: Set[EnumVal] = sealedSet[EnumVal]
  }

  val m = WeekDay.values.find(_.name == "Mon")
  println(s"Did we find Monday: $m")
  // -> Some(Mon)

  println(s"Is Mon before Tue? ${WeekDay.Mon < WeekDay.Tue}")
  // -> true
}
