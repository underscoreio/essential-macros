organization := "underscore.io"

name := "scala-macros"

version := "1.0.0"

val commonSettings = Seq(
  scalaVersion := "2.11.0",
  scalacOptions ++= Seq("-deprecation", "-feature"),
  libraryDependencies ++= Seq(
    "org.scala-lang" % "scala-reflect" % scalaVersion.value,
    "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.1",
    "org.specs2" %% "specs2" % "2.3.12" % "test"
  )
)

lazy val helloLib        = project.in(file("hello/lib")).settings(commonSettings : _*)

lazy val hello           = project.in(file("hello/app")).settings(commonSettings : _*).dependsOn(helloLib)

lazy val maximumLib      = project.in(file("maximum/lib")).settings(commonSettings : _*)

lazy val maximum         = project.in(file("maximum/app")).settings(commonSettings : _*).dependsOn(maximumLib)

lazy val printtreeLib    = project.in(file("printtree/lib")).settings(commonSettings : _*)

lazy val printtree       = project.in(file("printtree/app")).settings(commonSettings : _*).dependsOn(printtreeLib)

lazy val simpleassertLib = project.in(file("simpleassert/lib")).settings(commonSettings : _*)

lazy val simpleassert    = project.in(file("simpleassert/app")).settings(commonSettings : _*).dependsOn(simpleassertLib)

lazy val betterassertLib = project.in(file("betterassert/lib")).settings(commonSettings : _*)

lazy val betterassert    = project.in(file("betterassert/app")).settings(commonSettings : _*).dependsOn(betterassertLib)

lazy val printtypeLib    = project.in(file("printtype/lib")).settings(commonSettings : _*)

lazy val printtype       = project.in(file("printtype/app")).settings(commonSettings : _*).dependsOn(printtypeLib)

lazy val orderingsLib    = project.in(file("orderings/lib")).settings(commonSettings : _*)

lazy val orderings       = project.in(file("orderings/app")).settings(commonSettings : _*).dependsOn(orderingsLib)

lazy val whiteboxLib     = project.in(file("whitebox/lib")).settings(commonSettings : _*)

lazy val whitebox        = project.in(file("whitebox/app")).settings(commonSettings : _*).dependsOn(whiteboxLib)

lazy val validationLib   = project.in(file("validation/lib")).settings(commonSettings : _*)

lazy val validation      = project.in(file("validation/app")).settings(commonSettings : _*).dependsOn(validationLib)

lazy val csvLib          = project.in(file("csv/lib")).settings(commonSettings : _*)

lazy val csv             = project.in(file("csv/app")).settings(commonSettings : _*).dependsOn(csvLib)
