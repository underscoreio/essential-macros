import scala.language.experimental.macros

import scala.reflect.macros.blackbox.Context
import scala.util.{ Try => ScalaTry }

object PrintType {
  // These macros deal with two types of data structure:
  //
  //  - Types - objects representing the type of a variable
  //    or expression;
  //
  //  - Symbols - objects representing class, trait, method,
  //    or variable declarations.
  //
  // We obtain `Types` from type parameters on the macro
  // itself, and `Symbols` via `Types` or `Trees`, subject
  // to a couple of restrictions:
  //
  //  - we can only access `Symbol` information from a `Type`
  //    if we have enough information at compile time on how
  //    the type parameter is bound. For example, if we're
  //    inspecting a type parameter from a method definition,
  //    we don't yet know what types the method will be called
  //    with:
  //
  //    Example: def genericMethod[A] = genericMacro[A]
  //
  //  - we can only access `Symbol` information from `Trees`
  //    of type `SymTree`. These are essentially: trees
  //    representing declarations, and trees representing
  //    references to types and terms.

  // Macro that generates a `println` statement to print
  // information about the structure of type `A`. This
  // includes any type constructor and bound or unbound
  // type parameters:
  def printStructure[A]: Unit =
    macro PrintTypeMacros.printStructureMacro[A]

  // Macro that generates a `println` statement to print
  // declaration information of type `A`.
  //
  // This only prints meaningful output if we can inspect
  // `A` to get at its definition:
  def printSymbol[A]: Unit =
    macro PrintTypeMacros.printTypeSymbolMacro[A]

  // Macro that generates a `println` statement to print
  // declaration information about the type of a `value`.
  //
  // This only prints meaningful output if `value` is
  // represented by an expression that can be linked to
  // a declaration. Examples include method calls, field
  // references, identifiers, and method, field, class,
  // and trait declarations:
  def printSymbol(value: Any): Unit =
    macro PrintTypeMacros.printTermSymbolMacro

  // Macro that generates a `println` statement to print
  // information about each member declaration of type `A`.
  //
  // This only prints meaningful output if we can inspect
  // `A` to get at its definition:
  def printDecls[A]: Unit =
    macro PrintTypeMacros.printDeclsMacro[A]
}

class PrintTypeMacros(val c: Context) {
  import c.universe._

  def printStructureMacro[A: c.WeakTypeTag] = {
    val tpe = weakTypeOf[A]

    q"""
    println(${show(tpe)} + ".typeConstructor "                    + ${show(tpe.typeConstructor)})
    println(${show(tpe)} + ".typeArgs "                           + ${show(tpe.typeArgs)})
    println(${show(tpe)} + ".dealias.typeConstructor "            + ${show(tpe.dealias.typeConstructor)})
    println(${show(tpe)} + ".dealias.typeArgs "                   + ${show(tpe.dealias.typeArgs)})
    println(${show(tpe)} + ".dealias.typeConstructor.typeParams " + ${show(tpe.dealias.typeConstructor.typeParams)})
    println(${show(tpe)} + ".typeSymbol "                         + ${show(tpe.typeSymbol)})
    """
  }

  def printTypeSymbolMacro[A: c.WeakTypeTag]: c.Tree =
    printSymbol(weakTypeOf[A].typeSymbol, "")

  def printTermSymbolMacro(value: c.Tree): c.Tree =
    printSymbol(value.symbol, "")

  def printDeclsMacro[A: c.WeakTypeTag]: c.Tree = {
    val tpe = weakTypeOf[A]
    q"""
    println(${show(tpe)})
    ..${tpe.decls.map(printSymbol(_, "  "))}
    """
  }

  // Generate a print statement that prints information about
  // a `Symbol`. This essentially calls all the simple methods
  // from the `Symbol` API.
  //
  // There is a type hierarchy for symbols:
  //  - Symbol
  //     - TermSymbol - any term (value-level) declaration
  //        - MethodSymbol - any method (or constructor or field) declaration
  //        - ModuleSymbol - any singleton object declaration
  //     - TypeSymbol - any type-level declaration
  //        - ClassSymbol - class or trait declaration
  //
  // The code below works out what type the argument is, and prints
  // everything it can for that type:
  def printSymbol(decl: Symbol, prefix: String = ""): c.Tree = {
    // The code below buffers `println` statements in this mutable list.
    //
    // The final output of the macro is a block containing a sequence
    // of all the accumulated `printlns`:
    var exprs = Seq.empty[c.Tree]

    def write(str: String) =
      exprs = exprs :+ q"println($str)"

    // Print the name of the symbol:

    write(prefix + showDecl(decl))

    // Things we know for any `Symbol`:

    {
      var keywords = Seq.empty[String]
      if(decl.isTerm)                   keywords = keywords :+ "isTerm"
      if(decl.isType)                   keywords = keywords :+ "isType"
      if(decl.isClass)                  keywords = keywords :+ "isClass"
      if(decl.isMethod)                 keywords = keywords :+ "isMethod"
      if(decl.isModule)                 keywords = keywords :+ "isModule"
      if(decl.isAbstract)               keywords = keywords :+ "isAbstract"
      if(decl.isAbstractOverride)       keywords = keywords :+ "isAbstractOverride"
      if(decl.isFinal)                  keywords = keywords :+ "isFinal"
      if(decl.isImplementationArtifact) keywords = keywords :+ "isImplArtifact"
      if(decl.isImplicit)               keywords = keywords :+ "isImplicit"
      if(decl.isJava)                   keywords = keywords :+ "isJava"
      if(decl.isMacro)                  keywords = keywords :+ "isMacro"
      if(decl.isPackage)                keywords = keywords :+ "isPackage"
      if(decl.isPackageClass)           keywords = keywords :+ "isPackageClass"
      if(decl.isParameter)              keywords = keywords :+ "isParameter"
      if(decl.isPrivate)                keywords = keywords :+ "isPrivate"
      if(decl.isPrivateThis)            keywords = keywords :+ "isPrivateThis"
      if(decl.isProtected)              keywords = keywords :+ "isProtected"
      if(decl.isProtectedThis)          keywords = keywords :+ "isProtectedThis"
      if(decl.isPublic)                 keywords = keywords :+ "isPublic"
      if(decl.isSpecialized)            keywords = keywords :+ "isSpecialized"
      if(decl.isStatic)                 keywords = keywords :+ "isStatic"
      if(decl.isSynthetic)              keywords = keywords :+ "isSynthetic"
      if(decl.isModuleClass)            keywords = keywords :+ "isModuleClass"
      if(!decl.alternatives.isEmpty)    keywords = keywords :+ "alternatives = " + decl.alternatives
      if(!decl.annotations.isEmpty)     keywords = keywords :+ "annotations = " + decl.annotations
      if(true)                          keywords = keywords :+ "fullName = " + decl.fullName
      if(true)                          keywords = keywords :+ "info = " + decl.info.toString.replaceAll("\n *", " ")
      if(!decl.overrides.isEmpty)       keywords = keywords :+ "overrides = " + decl.overrides
      if(true)                          keywords = keywords :+ "owner = " + decl.owner
      if(decl.companion != NoSymbol)    keywords = keywords :+ "companion = " + decl.companion
      if(true)                          keywords = keywords :+ "typeSignature = " + decl.typeSignature.toString.replaceAll("\n *", " ")
      write(prefix + "  asSymbol")
      keywords.foreach(kw => write(prefix + "    " + kw))
    }

    // Things that are defined for any `TypeSymbol`:

    if(decl.isType) {
      val tipe = decl.asType
      var keywords = Seq.empty[String]
      if(tipe.isAliasType)         keywords = keywords :+ "isAliasType"
      if(tipe.isContravariant)     keywords = keywords :+ "isContravariant"
      if(tipe.isCovariant)         keywords = keywords :+ "isCovariant"
      if(tipe.isExistential)       keywords = keywords :+ "isExistential"
      if(true)                     keywords = keywords :+ "toType = " + tipe.toType
      if(true)                     keywords = keywords :+ "toTypeConstructor = " + tipe.toTypeConstructor
      if(!tipe.typeParams.isEmpty) keywords = keywords :+ "typeParams = " + tipe.typeParams
      write(prefix + "  asType")
      keywords.foreach(kw => write(prefix + "    " + kw))
    }

    // Things that are defined for any `TermSymbol`:

    if(decl.isTerm) {
      val term = decl.asTerm
      var keywords = Seq.empty[String]
      if(term.isAccessor)               keywords = keywords :+ "isAccessor"
      if(term.isByNameParam)            keywords = keywords :+ "isByNameParam"
      if(term.isCaseAccessor)           keywords = keywords :+ "isCaseAccessor"
      if(term.isGetter)                 keywords = keywords :+ "isGetter"
      if(term.isLazy)                   keywords = keywords :+ "isLazy"
      if(term.isOverloaded)             keywords = keywords :+ "isOverloaded"
      if(term.isParamAccessor)          keywords = keywords :+ "isParamAccessor"
      if(term.isParamWithDefault)       keywords = keywords :+ "isParamWithDefault"
      if(term.isSetter)                 keywords = keywords :+ "isSetter"
      if(term.isStable)                 keywords = keywords :+ "isStable"
      if(term.isVal)                    keywords = keywords :+ "isVal"
      if(term.isVar)                    keywords = keywords :+ "isVar"
      if(term.getter != NoSymbol)       keywords = keywords :+ "getter = " + term.getter
      if(term.setter != NoSymbol)       keywords = keywords :+ "setter = " + term.setter
      ScalaTry {
        if(term.accessed != NoSymbol)   keywords = keywords :+ "accessed = " + term.accessed
      }
      write(prefix + "  asTerm")
      keywords.foreach(kw => write(prefix + "    " + kw))
    }

    // Things that are defined for any `ClassSymbol`:

    if(decl.isClass) {
      val clss = decl.asClass
      var keywords = Seq.empty[String]
      if(clss.isCaseClass)                     keywords = keywords :+ "isCaseClass"
      if(clss.isDerivedValueClass)             keywords = keywords :+ "isDerivedValueClass"
      if(clss.isNumeric)                       keywords = keywords :+ "isNumeric"
      if(clss.isPrimitive)                     keywords = keywords :+ "isPrimitive"
      if(clss.isSealed)                        keywords = keywords :+ "isSealed"
      if(clss.isTrait)                         keywords = keywords :+ "isTrait"
      if(!clss.baseClasses.isEmpty)            keywords = keywords :+ "baseClasses = " + clss.baseClasses
      if(!clss.knownDirectSubclasses.isEmpty)  keywords = keywords :+ "knownDirectSubclasses = " + clss.knownDirectSubclasses
      if(clss.module != NoSymbol)              keywords = keywords :+ "module = " + clss.module
      if(clss.primaryConstructor != NoSymbol)  keywords = keywords :+ "primaryConstructor = " + clss.primaryConstructor
      if(!clss.typeParams.isEmpty)             keywords = keywords :+ "typeParams = " + clss.typeParams
      if(clss.selfType != clss.toType)         keywords = keywords :+ "selfType = " + clss.selfType
      write(prefix + "  asClass")
      keywords.foreach(kw => write(prefix + "    " + kw))
    }

    // Things that are defined for any `MethodSymbol`:

    if(decl.isMethod) {
      val meth = decl.asMethod
      var keywords = Seq.empty[String]
      if(meth.isConstructor)            keywords = keywords :+ "isConstructor"
      if(meth.isPrimaryConstructor)     keywords = keywords :+ "isPrimaryConstructor"
      if(meth.isVarargs)                keywords = keywords :+ "isVarargs"
      if(!meth.exceptions.isEmpty)      keywords = keywords :+ "exceptions = " + meth.exceptions
      if(!meth.paramLists.isEmpty)      keywords = keywords :+ "paramLists = " + meth.paramLists
      if(true)                          keywords = keywords :+ "returnType = " + meth.returnType
      if(!meth.typeParams.isEmpty)      keywords = keywords :+ "typeParams = " + meth.typeParams
      write(prefix + "  asMethod")
      keywords.foreach(kw => write(prefix + "    " + kw))
    }

    // Things that are defined for any `ModuleSymbol`:

    if(decl.isModule) {
      val modl = decl.asModule
      var keywords = Seq.empty[String]
      if(modl.moduleClass != NoSymbol)  keywords = keywords :+ "moduleClass"
      write(prefix + "  asModule")
      keywords.foreach(kw => write(prefix + "    " + kw))
    }

    q"..$exprs"
  }
}
