package com.developersam.pl.sapl.ast.type

import com.developersam.pl.sapl.ast.protocol.Printable
import com.developersam.pl.sapl.codegen.IndentationQueue

/**
 * [TypeDeclaration] represents a set of supported type expression in type declaration.
 */
sealed class TypeDeclaration : Printable {

    final override fun toString(): String = asIndentedSourceCode

    /**
     * [Variant] represents the constructor and an optional associated type all defined in [map].
     */
    data class Variant(
            val map: Map<String, TypeExpr?>
    ) : TypeDeclaration() {

        override fun prettyPrint(q: IndentationQueue): Unit =
                map.forEach { (name, expr) ->
                    val line = StringBuilder()
                            .append("| ").append(name)
                            .apply {
                                if (expr != null) {
                                    append(" of ").append(expr.toString())
                                }
                            }.toString()
                    q.addLine(line = line)
                }

    }

    /**
     * [Struct] represents the struct declaration with all those info defined in [map].
     */
    data class Struct(val map: Map<String, TypeExpr>) : TypeDeclaration() {

        override fun prettyPrint(q: IndentationQueue): Unit =
                map.forEach { (name, expr) -> q.addLine(line = "$name: $expr;") }

    }

}