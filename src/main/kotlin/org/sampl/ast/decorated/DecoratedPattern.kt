package org.sampl.ast.decorated

import org.sampl.ast.type.TypeExpr

/**
 * [DecoratedPattern] is the pattern with appropriate and correct type decoration.
 */
sealed class DecoratedPattern {

    /**
     * [Variant] with an optional [associatedVariableType] represents the variant
     * pattern with [variantIdentifier] and potentially an [associatedVariable].
     */
    data class Variant(
            val variantIdentifier: String, val associatedVariable: String? = null,
            val associatedVariableType: TypeExpr? = null
    ) : DecoratedPattern()

    /**
     * [Variable] with [type] represents a variable that matches everything.
     */
    data class Variable(val identifier: String, val type: TypeExpr) : DecoratedPattern()

    /**
     * [WildCard] represents a wildcard but does not bound to anything.
     */
    object WildCard : DecoratedPattern()

}

