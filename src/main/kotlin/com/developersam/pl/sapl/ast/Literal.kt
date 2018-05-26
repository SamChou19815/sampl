package com.developersam.pl.sapl.ast

/**
 * [Literal] represents a set of supported literal.
 */
sealed class Literal : AstNode

object UnitLiteral: Literal()
data class IntLiteral(val value: Long) : Literal()
data class FloatLiteral(val value: Double) : Literal()
data class BoolLiteral(val value: Boolean) : Literal()
data class CharLiteral(val value: Char) : Literal()
data class StringLiteral(val value: String) : Literal()

/**
 * [LiteralBuilder] builds literals.
 */
object LiteralBuilder {
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
