package org.sampl.ast.type

import org.sampl.ast.protocol.PrettyPrintable
import org.sampl.codegen.IndentationQueue

/**
 * [TypeDeclaration] represents a set of supported type expression in type declaration.
 */
sealed class TypeDeclaration : PrettyPrintable {

    /**
     * [isEmpty] reports whether the declaration is an empty struct.
     */
    val isEmpty: Boolean
        get() = when (this) {
            is Variant -> false
            is Struct -> map.isEmpty()
        }

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

        override fun prettyPrint(q: IndentationQueue) {
            val l = map.size
            var i = 1
            for ((name, expr) in map) {
                if (i == l) {
                    q.addLine(line = "$name: $expr")
                } else {
                    q.addLine(line = "$name: $expr,")
                }
                i++
            }
        }

    }

}
