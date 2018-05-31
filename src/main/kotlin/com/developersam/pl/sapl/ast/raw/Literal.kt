package com.developersam.pl.sapl.ast.raw

import com.developersam.pl.sapl.ast.TypeExprInAnnotation
import com.developersam.pl.sapl.ast.boolTypeExpr
import com.developersam.pl.sapl.ast.charTypeExpr
import com.developersam.pl.sapl.ast.floatTypeExpr
import com.developersam.pl.sapl.ast.intTypeExpr
import com.developersam.pl.sapl.ast.stringTypeExpr
import com.developersam.pl.sapl.ast.unitTypeExpr

/**
 * [Literal] represents a set of supported literal.
 */
internal sealed class Literal {

    /**
     * [inferredType] reports the inferred type from the literal.
     */
    abstract val inferredType: TypeExprInAnnotation

}

/**
 * [UnitLiteral] is the literal for unit.
 */
internal object UnitLiteral : Literal() {
    override val inferredType: TypeExprInAnnotation =
            unitTypeExpr
}

/**
 * [IntLiteral] is the literal for int with [value].
 */
internal data class IntLiteral(val value: Long) : Literal() {
    override val inferredType: TypeExprInAnnotation =
            intTypeExpr
}

/**
 * [FloatLiteral] is the literal for float with [value].
 */
internal data class FloatLiteral(val value: Double) : Literal() {
    override val inferredType: TypeExprInAnnotation =
            floatTypeExpr
}

/**
 * [BoolLiteral] is the literal for bool with [value].
 */
internal data class BoolLiteral(val value: Boolean) : Literal() {
    override val inferredType: TypeExprInAnnotation =
            boolTypeExpr
}

/**
 * [CharLiteral] is the literal for char with [value].
 */
internal data class CharLiteral(val value: Char) : Literal() {
    override val inferredType: TypeExprInAnnotation =
            charTypeExpr
}

/**
 * [StringLiteral] is the literal for string with [value].
 */
internal data class StringLiteral(val value: String) : Literal() {
    override val inferredType: TypeExprInAnnotation =
            stringTypeExpr
}

/**
 * [LiteralBuilder] builds literals.
 */
internal object LiteralBuilder {
    /**
     * [from] creates a literal from a [text].
     */
    fun from(text: String): Literal {
        if (text == "()") {
            return UnitLiteral
        }
        val longOpt = text.toLongOrNull()
        if (longOpt != null) {
            return IntLiteral(value = longOpt)
        }
        val doubleOpt = text.toDoubleOrNull()
        if (doubleOpt != null) {
            return FloatLiteral(value = doubleOpt)
        }
        return when (text) {
            "true" -> BoolLiteral(value = true)
            "false" -> BoolLiteral(value = false)
            else -> {
                if (text.length == 3 && text[0] == '\'' && text[2] == '\'') {
                    CharLiteral(value = text[1])
                } else {
                    StringLiteral(value = text.substring(range = 1 until (text.length - 1)))
                }
            }
        }
    }
}
