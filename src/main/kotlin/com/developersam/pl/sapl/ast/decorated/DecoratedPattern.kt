package com.developersam.pl.sapl.ast.decorated

import com.developersam.pl.sapl.ast.protocol.Transpilable
import com.developersam.pl.sapl.codegen.TranspilerVisitor
import com.developersam.pl.sapl.ast.type.TypeExpr
import com.developersam.pl.sapl.codegen.IndentationQueue

/**
 * [DecoratedPattern] is the pattern with appropriate and correct type decoration.
 */
sealed class DecoratedPattern : Transpilable {

    /**
     * [asSourceCode] returns itself in source code form.
     */
    abstract val asSourceCode: String

    override fun acceptTranspilation(q: IndentationQueue, visitor: TranspilerVisitor): Unit =
            visitor.visit(q = q, pattern = this)

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

