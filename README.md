Essential Macros Code Examples
==============================

Copyright 2014 Dave Gurnell of Underscore
http://underscore.io

These example projects are companion material for the Underscore [Essential Scala Macros] course
as well as for the author's talk, [Macros for the Rest of Us], presented at [ScalaDays 2014].

Licensed under the Apache License v2.0
http://www.apache.org/licenses/LICENSE-2.0.html

[Essential Scala Macros]: http://underscore.io/courses/essential-macros.html
[Macros for the Rest of Us]: http://www.scaladays.org/#schedule/Macros-for-the-Rest-of-Us
[ScalaDays 2014]: http://scaladays.org

**If you're interested in this content, sign up to our mailing list at [http://underscore.io](http://underscore.io/newsletter.html) where we post updates and code samples about Scala, Scala Macros, and functional programming in general.**

The Examples
------------

Each example is split into two projects, `app` and `lib`. `lib` defines the macros and `app` makes use of them.

 - **hello** - Hello world written as a macro. The macro embeds a compilation timestamp
   in the printed message to demonstrate that it is executed at compile time. This is
   contrasted with regular code that prints a timestamp at run time.

 - **maximum** - Simple project demonstrating basic setup. The macro itself, `maximum`,
   isn't particularly useful, but the project serves as a good example of the various
   concepts in play.

 - **printtree** - Macro that uses `showCode` and `showRaw` to print the desugared syntax
   and underlying tree structure of arbitrary snippets of Scala code. Good for doing research
   in advance of writing new macros.

 - **simpleassert** - Macro demonstrating simple pattern matching on trees using quasiquotes.
   Slightly improved version of Scala's built-in `assert` method that prints various values
   involved in the assertion.

 - **betterassert** - Macro demonstrating more advanced tree inspection using pattern matching
   and tree traversal. Improved version of `simpleassert` that prints useful debugging
   information in a much wider range of cases.

 - **printtype** - Family of generic macros that print a variety of information about their
   type parameters.

 - **orderings** - Code generation macro that inspects a type and creates an object allowing
   sorting by any of its fields. This technique is useful when writing, for example, a web
   service that allows users to sort a database by any field in the returned data.
   
 - **enumerations** - Generating the set of instances of a sealed trait. Useful for 
   rolling your own enumerations via sealed case objects.  

 - **whitebox** - Simple project demonstrating the fundamental difference between whitebox
   and blackbox macros. See comments in the main application file for details.

 - **validation** - Sketch of a codebase for a validation library that automatically tags
   errors using the names of the erroneous fields. One significant property is that the macro
   is implemented as a chainable method call rather than as a top-level function.

 - **csv** - Sketch of a type-class-based codebase for CSV serialization. An implicit macro
   is used to support automatic materialization of type class instances for case classes.
