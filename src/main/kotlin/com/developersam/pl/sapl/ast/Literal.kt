package com.developersam.pl.sapl.ast

import com.developersam.pl.sapl.exceptions.InvalidLiteralError
import org.apache.commons.text.StringEscapeUtils

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
         *
         * If the literal in [text] is bad, it will throw an [InvalidLiteralError].
         */
        fun from(text: kotlin.String): Literal {
            val unescaped: kotlin.String = StringEscapeUtils.unescapeJava(text)
            when (unescaped) {
                "()" -> return Literal.Unit
                "true" -> return Literal.Bool(value = true)
                "false" -> return Literal.Bool(value = false)
                else -> {
                    unescaped.toLongOrNull()?.let { return Literal.Int(value = it) }
                    unescaped.toDoubleOrNull()?.let { return Literal.Float(value = it) }
                    val len = unescaped.length
                    if (len < 2) {
                        throw InvalidLiteralError(invalidLiteral = text)
                    }
                    val first = unescaped[0]
                    val last = unescaped[len - 1]
                    val betweenQuotes = unescaped.substring(startIndex = 1, endIndex = len - 1)
                    return if (first == '\'' && last == '\'') {
                        Literal.Char(value = betweenQuotes[0])
                    } else if (first == '"' && last == '"') {
                        Literal.String(value = betweenQuotes)
                    } else {
                        throw InvalidLiteralError(invalidLiteral = text)
                    }
                }
            }
        }
    }

}
