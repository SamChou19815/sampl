package com.developersam.pl.sapl.ast.type

import com.developersam.pl.sapl.ast.protocol.Printable
import com.developersam.pl.sapl.config.IndentationStrategy

/**
 * [TypeDeclaration] represents a set of supported type expression in type declaration.
 */
sealed class TypeDeclaration : Printable {

    /**
     * [Variant] represents the constructor and an optional associated type all defined in [map].
     */
    data class Variant(
            val map: Map<String, TypeExpr?>
    ) : TypeDeclaration() {

        override fun prettyPrint(level: Int, builder: StringBuilder) {
            for ((name, expr) in map) {
                IndentationStrategy.indent2(level, builder).append("| ").append(name)
                if (expr == null) {
                    builder.append('\n')
                } else {
                    builder.append(" of ")
                    expr.prettyPrint(builder = builder)
                    builder.append('\n')
                }
            }
        }

    }

    /**
     * [Struct] represents the struct declaration with all those info defined in [map].
     */
    data class Struct(val map: Map<String, TypeExpr>) : TypeDeclaration() {

        override fun prettyPrint(level: Int, builder: StringBuilder) {
            for ((name, expr) in map) {
                IndentationStrategy.indent2(level, builder)
                        .append(name).append(": ").append(expr).append(";\n")
            }
        }
    }

}
