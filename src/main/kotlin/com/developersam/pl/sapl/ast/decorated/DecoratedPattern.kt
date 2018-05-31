package com.developersam.pl.sapl.ast.decorated

import com.developersam.pl.sapl.ast.TypeExprInAnnotation

/**
 * [DecoratedPattern] is the pattern with appropriate and correct type decoration.
 */
internal sealed class DecoratedPattern {

    /**
     * [Variant] with an optional [associatedVariableType] represents the variant
     * pattern with [variantIdentifier] and potentially an [associatedVariable].
     */
    internal data class Variant(
            val variantIdentifier: String, val associatedVariable: String? = null,
            val associatedVariableType: TypeExprInAnnotation? = null
    ) : DecoratedPattern()

    /**
     * [Variable] with [type] represents a variable that matches everything.
     */
    internal data class Variable(
            val identifier: String, val type: TypeExprInAnnotation
    ) : DecoratedPattern()

    /**
     * [WildCard] represents a wildcard but does not bound to anything.
     */
    internal object WildCard : DecoratedPattern()

}

