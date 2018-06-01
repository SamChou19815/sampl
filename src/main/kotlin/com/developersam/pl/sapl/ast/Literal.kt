package com.developersam.pl.sapl.ast

/**
 * [Literal] represents a set of supported literal.
 *
 * @param inferredType the inferred type from the literal.
 */
sealed class Literal(val inferredType: TypeExpr) {

    /**
     * [Unit] is the literal for unit.
     */
    object Unit : Literal(inferredType = unitTypeExpr)

    /**
     * [Int] is the literal for int with [value].
     */
    data class Int(val value: Long) : Literal(inferredType = intTypeExpr)

    /**
     * [Float] is the literal for float with [value].
     */
    data class Float(val value: Double) : Literal(inferredType = floatTypeExpr)

    /**
     * [Bool] is the literal for bool with [value].
     */
    data class Bool(val value: Boolean) : Literal(inferredType = boolTypeExpr)

    /**
     * [Char] is the literal for char with [value].
     */
    data class Char(val value: kotlin.Char) : Literal(inferredType = charTypeExpr)

    /**
     * [String] is the literal for string with [value].
     */
    data class String(val value: kotlin.String) : Literal(inferredType = stringTypeExpr)

    companion object {
        /**
         * [from] creates a literal from a [text].
         */
        fun from(text: kotlin.String): Literal {
            if (text == "()") {
                return Literal.Unit
            }
            val longOpt = text.toLongOrNull()
            if (longOpt != null) {
                return Literal.Int(value = longOpt)
            }
            val doubleOpt = text.toDoubleOrNull()
            if (doubleOpt != null) {
                return Literal.Float(value = doubleOpt)
            }
            return when (text) {
                "true" -> Literal.Bool(value = true)
                "false" -> Literal.Bool(value = false)
                else -> {
                    if (text.length == 3 && text[0] == '\'' && text[2] == '\'') {
                        Literal.Char(value = text[1])
                    } else {
                        Literal.String(value = text.substring(range = 1 until (text.length - 1)))
                    }
                }
            }
        }
    }

}
