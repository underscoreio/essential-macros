import scala.util.parsing.combinator._

object PrettyPrint extends JavaTokenParsers {
  override def skipWhitespace = true

  sealed trait Node {
    def prettyPrint(indent: Int): String
  }

  final case class Func(name: String, args: Seq[Node]) extends Node {
    def prettyPrint(indent: Int): String =
      (" " * indent) + name + (args match {
        case Seq()                                =>  "()"
        case Seq(node : Select)                   => s"(${node.prettyPrint(0)})"
        case Seq(node : Literal)                  => s"(${node.prettyPrint(0)})"
        case Seq(Func(name, Seq(node : Select)))  => s"($name(${node.prettyPrint(0)}))"
        case Seq(Func(name, Seq(node : Literal))) => s"($name(${node.prettyPrint(0)}))"
        case args => args
          .map(_.prettyPrint(indent + 2))
          .mkString("(\n", ",\n", "\n" + (" " * indent) + ")")
      })
  }

  final case class Select(ids: Seq[String]) extends Node {
    def prettyPrint(indent: Int): String = (" " * indent) + ids.mkString(".")
  }

  final case class Literal(text: String) extends Node {
    def prettyPrint(indent: Int): String = (" " * indent) + text
  }

  def node: Parser[Node] =
    func | select | literal

  def func: Parser[Func] =
    ident ~ "(" ~ repsep(node, ",") ~ ")" ^^ {
      case name ~ _ ~ args ~ _ =>
        Func(name, args)
    }

  def select: Parser[Select] =
    rep1sep(ident, ".") ^^ {
      case ids =>
        Select(ids)
    }

  def literal: Parser[Literal] =
    (wholeNumber | decimalNumber | stringLiteral | floatingPointNumber | "()") ^^ {
      case term =>
        Literal(term.toString)
    }

  def prettify(in: String) =
    parseAll(node, in) match {
      case Success(node, _)  => node.prettyPrint(0)
      case NoSuccess(msg, _) => in
    }
}