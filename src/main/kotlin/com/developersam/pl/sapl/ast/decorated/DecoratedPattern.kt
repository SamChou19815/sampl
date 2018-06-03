package com.developersam.pl.sapl.ast.decorated

import com.developersam.pl.sapl.ast.protocol.Printable
import com.developersam.pl.sapl.ast.type.TypeExpr

/**
 * [DecoratedPattern] is the pattern with appropriate and correct type decoration.
 */
sealed class DecoratedPattern : Printable {

    final override fun toString(): String = prettyPrint()

    /**
     * [Variant] with an optional [associatedVariableType] represents the variant
     * pattern with [variantIdentifier] and potentially an [associatedVariable].
     */
    data class Variant(
            val variantIdentifier: String, val associatedVariable: String? = null,
            val associatedVariableType: TypeExpr? = null
    ) : DecoratedPattern() {

        override fun prettyPrint(level: Int, builder: StringBuilder) {
            builder.append(variantIdentifier)
            if (associatedVariable != null) {
                builder.append(" of ").append(associatedVariable)
            }
        }

    }

    /**
     * [Variable] with [type] represents a variable that matches everything.
     */
    data class Variable(
            val identifier: String, val type: TypeExpr
    ) : DecoratedPattern()  {

        override fun prettyPrint(level: Int, builder: StringBuilder) {
            builder.append(identifier)
        }

    }

    /**
     * [WildCard] represents a wildcard but does not bound to anything.
     */
    object WildCard : DecoratedPattern() {

        override fun prettyPrint(level: Int, builder: StringBuilder) {
            builder.append("_")
        }

    }

}

