package com.developersam.pl.sapl.ast

import com.developersam.pl.sapl.util.toTable

/**
 * [BinaryOperator] is a collection of supported binary operators.
 *
 * @param symbol the text symbol defined in the lexer.
 */
enum class BinaryOperator(val symbol: String) {

    /**
     * Shift left.
     */
    SHL(symbol = "<<"),
    /**
     * Shift right.
     */
    SHR(symbol = ">>"),
    /**
     * Unsigned shift right.
     */
    USHR(symbol = ">>>"),
    /**
     * Xor.
     */
    XOR(symbol = "xor"),
    /**
     * Logical And.
     */
    LAND(symbol = "&"),
    /**
     * Logical Or.
     */
    LOR(symbol = "|"),
    /**
     * Integer multiplication.
     */
    MUL(symbol = "*"),
    /**
     * Integer division.
     */
    DIV(symbol = "/"),
    /**
     * Integer mod.
     */
    MOD(symbol = "%"),
    /**
     * Float multiplication.
     */
    F_MUL(symbol = "*."),
    /**
     * Float division.
     */
    F_DIV(symbol = "/."),
    /**
     * Integer addition.
     */
    PLUS(symbol = "+"),
    /**
     * Integer subtraction.
     */
    MINUS(symbol = "-"),
    /**
     * Float addition.
     */
    F_PLUS(symbol = "+."),
    /**
     * Float subtraction.
     */
    F_MINUS(symbol = "-."),
    /**
     * String concatenation.
     */
    STR_CONCAT(symbol = "^"),
    /**
     * Referential equality.
     */
    REF_EQ(symbol = "==="),
    /**
     * Structural equality.
     */
    STRUCT_EQ(symbol = "=="),
    /**
     * Less then.
     */
    LT(symbol = "<"),
    /**
     * Less then or equal to.
     */
    LE(symbol = "<="),
    /**
     * Greater than.
     */
    GT(symbol = ">"),
    /**
     * Greater than or equal to.
     */
    GE(symbol = ">="),
    /**
     * Referential NOT equality.
     */
    REF_NE(symbol = "!=="),
    /**
     * Structural NOT equality.
     */
    STRUCT_NE(symbol = "!="),
    /**
     * Conjunction.
     */
    AND(symbol = "&&"),
    /**
     * Disjunction.
     */
    OR(symbol = "||");

    companion object {
        /**
         * [symbolTable] is the map that converts a string to the enum value.
         */
        private val symbolTable: Map<String, BinaryOperator> =
                BinaryOperator.values().toTable(BinaryOperator::symbol)

        /**
         * [fromRaw] converts a raw string binary operator to the binary operator in the enum mode.
         *
         * @param text the binary operator in the string form.
         * @throws IllegalArgumentException if the given [text] is not a binary operator.
         */
        fun fromRaw(text: String): BinaryOperator = symbolTable[text]
                ?: throw IllegalArgumentException("Not a supported binary operator.")
    }

}