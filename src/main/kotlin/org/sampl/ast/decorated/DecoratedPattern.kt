package org.sampl.ast.decorated

import org.sampl.ast.type.TypeExpr

/**
 * [DecoratedPattern] is the pattern with appropriate and correct type decoration.
 */
sealed class DecoratedPattern {

    /**
     * [asSourceCode] returns itself in source code form.
     */
    abstract val asSourceCode: String

    /**
     * [Variant] with an optional [associatedVariableType] represents the variant
     * pattern with [variantIdentifier] and potentially an [associatedVariable].
     */
    data class Variant(
            val variantIdentifier: String, val associatedVariable: String? = null,
            val associatedVariableType: TypeExpr? = null
    ) : DecoratedPattern() {

        override val asSourceCode: String
            get() = StringBuilder().apply {
                append(variantIdentifier)
                if (associatedVariable != null) {
                    append(" of ").append(associatedVariable)
                }
            }.toString()

    }

    /**
     * [Variable] with [type] represents a variable that matches everything.
     */
    data class Variable(val identifier: String, val type: TypeExpr) : DecoratedPattern() {
        override val asSourceCode: String get() = identifier
    }

    /**
     * [WildCard] represents a wildcard but does not bound to anything.
     */
    object WildCard : DecoratedPattern() {
        override val asSourceCode: String get() = "_"
    }

}

