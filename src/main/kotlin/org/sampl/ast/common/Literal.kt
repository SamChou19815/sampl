package org.sampl.ast.common

import org.apache.commons.text.StringEscapeUtils
import org.sampl.ast.type.TypeExpr
import org.sampl.ast.type.boolTypeExpr
import org.sampl.ast.type.charTypeExpr
import org.sampl.ast.type.floatTypeExpr
import org.sampl.ast.type.intTypeExpr
import org.sampl.ast.type.stringTypeExpr
import org.sampl.ast.type.unitTypeExpr
import org.sampl.exceptions.InvalidLiteralError

/**
 * [Literal] represents a set of supported literal.
 *
 * @param inferredType the inferred type from the literal.
 */
sealed class Literal(val inferredType: TypeExpr) {

    /**
     * [Unit] is the literal for unit.
     */
    object Unit : Literal(inferredType = unitTypeExpr) {

        override fun toString(): kotlin.String = "()"

    }

    /**
     * [Int] is the literal for int with [value].
     */
    data class Int(val value: Long) : Literal(inferredType = intTypeExpr) {

        override fun toString(): kotlin.String = value.toString()

    }

    /**
     * [Float] is the literal for float with [value].
     */
    data class Float(val value: Double) : Literal(inferredType = floatTypeExpr) {

        override fun toString(): kotlin.String = value.toString()

    }

    /**
     * [Bool] is the literal for bool with [value].
     */
    data class Bool(val value: Boolean) : Literal(inferredType = boolTypeExpr) {

        override fun toString(): kotlin.String = value.toString()

    }

    /**
     * [Char] is the literal for char with [value].
     */
    data class Char(val value: kotlin.Char) : Literal(inferredType = charTypeExpr) {

        override fun toString(): kotlin.String = "'$value'"

    }

    /**
     * [String] is the literal for string with [value].
     */
    data class String(val value: kotlin.String) : Literal(inferredType = stringTypeExpr) {

        override fun toString(): kotlin.String = "\"$value\""

    }

    companion object {

        /**
         * [from] creates a literal from a [text].
         *
         * If the literal in [text] is bad, it will throw an [InvalidLiteralError].
         */
        fun from(text: kotlin.String): Literal {
            val unescaped: kotlin.String = StringEscapeUtils.unescapeJava(text)
            when (unescaped) {
                "()" -> return Unit
                "true" -> return Bool(value = true)
                "false" -> return Bool(value = false)
                else -> {
                    unescaped.toLongOrNull()?.let { return Int(value = it) }
                    unescaped.toDoubleOrNull()?.let { return Float(value = it) }
                    val len = unescaped.length
                    if (len < 2) {
                        throw InvalidLiteralError(invalidLiteral = text)
                    }
                    val first = unescaped[0]
                    val last = unescaped[len - 1]
                    val betweenQuotes = unescaped.substring(startIndex = 1, endIndex = len - 1)
                    return if (first == '\'' && last == '\'') {
                        Char(value = betweenQuotes[0])
                    } else if (first == '"' && last == '"') {
                        String(value = betweenQuotes)
                    } else {
                        throw InvalidLiteralError(invalidLiteral = text)
                    }
                }
            }
        }
    }

}
