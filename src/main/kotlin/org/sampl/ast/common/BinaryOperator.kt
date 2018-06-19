package org.sampl.ast.common

import org.sampl.util.toTable

/**
 * [BinaryOperator] is a collection of supported binary operators.
 *
 * @param symbol the text symbol defined in the lexer.
 * @param precedenceLevel smaller this number, higher the precedence.
 */
enum class BinaryOperator(val symbol: String, val precedenceLevel: Int) {

    /**
     * Shift left.
     */
    SHL(symbol = "shl", precedenceLevel = 0),
    /**
     * Shift right.
     */
    SHR(symbol = "shr", precedenceLevel = 0),
    /**
     * Unsigned shift right.
     */
    USHR(symbol = "ushr", precedenceLevel = 0),
    /**
     * Xor.
     */
    XOR(symbol = "xor", precedenceLevel = 0),
    /**
     * Logical And.
     */
    LAND(symbol = "&", precedenceLevel = 0),
    /**
     * Logical Or.
     */
    LOR(symbol = "|", precedenceLevel = 0),
    /**
     * Integer multiplication.
     */
    MUL(symbol = "*", precedenceLevel = 1),
    /**
     * Integer division.
     */
    DIV(symbol = "/", precedenceLevel = 1),
    /**
     * Integer mod.
     */
    MOD(symbol = "%", precedenceLevel = 1),
    /**
     * Float multiplication.
     */
    F_MUL(symbol = "*.", precedenceLevel = 1),
    /**
     * Float division.
     */
    F_DIV(symbol = "/.", precedenceLevel = 1),
    /**
     * Integer addition.
     */
    PLUS(symbol = "+", precedenceLevel = 2),
    /**
     * Integer subtraction.
     */
    MINUS(symbol = "-", precedenceLevel = 2),
    /**
     * Float addition.
     */
    F_PLUS(symbol = "+.", precedenceLevel = 2),
    /**
     * Float subtraction.
     */
    F_MINUS(symbol = "-.", precedenceLevel = 2),
    /**
     * String concatenation.
     */
    STR_CONCAT(symbol = "^", precedenceLevel = 2),
    /**
     * Less then.
     */
    LT(symbol = "<", precedenceLevel = 3),
    /**
     * Less then or equal to.
     */
    LE(symbol = "<=", precedenceLevel = 3),
    /**
     * Greater than.
     */
    GT(symbol = ">", precedenceLevel = 3),
    /**
     * Greater than or equal to.
     */
    GE(symbol = ">=", precedenceLevel = 3),
    /**
     * Structural equality.
     */
    STRUCT_EQ(symbol = "==", precedenceLevel = 4),
    /**
     * Structural NOT equality.
     */
    STRUCT_NE(symbol = "!=", precedenceLevel = 4),
    /**
     * Conjunction.
     */
    AND(symbol = "&&", precedenceLevel = 5),
    /**
     * Disjunction.
     */
    OR(symbol = "||", precedenceLevel = 6);

    companion object {
        /**
         * [symbolTable] is the map that converts a string to the enum value.
         */
        @JvmStatic
        private val symbolTable: Map<String, BinaryOperator> =
                BinaryOperator.values().toTable(BinaryOperator::symbol)

        /**
         * [fromRaw] converts a raw string binary operator to the binary operator in the enum mode.
         *
         * @param text the binary operator in the string form.
         * @throws IllegalArgumentException if the given [text] is not a binary operator.
         */
        @JvmStatic
        fun fromRaw(text: String): BinaryOperator = symbolTable[text]
                ?: throw IllegalArgumentException("Not a supported binary operator.")
    }

}