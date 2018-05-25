package com.developersam.pl.sapl.ast

enum class BinaryOperator {
    /**
     * Shift left.
     */
    SHL,
    /**
     * Shift right.
     */
    SHR,
    /**
     * Unsigned shift right.
     */
    USHR,
    /**
     * Xor.
     */
    XOR,
    /**
     * Logical And.
     */
    LAND,
    /**
     * Logical Or.
     */
    LOR,
    /**
     * Integer multiplication.
     */
    MUL,
    /**
     * Integer division.
     */
    DIV,
    /**
     * Integer mod.
     */
    MOD,
    /**
     * Float multiplication.
     */
    F_MUL,
    /**
     * Float division.
     */
    F_DIV,
    /**
     * Integer addition.
     */
    PLUS,
    /**
     * Integer subtraction.
     */
    MINUS,
    /**
     * Float addition.
     */
    F_PLUS,
    /**
     * Float subtraction.
     */
    F_MINUS,
    /**
     * Referential equality.
     */
    REF_EQ,
    /**
     * Structural equality.
     */
    STRUCT_EQ,
    /**
     * Less then.
     */
    LT,
    /**
     * Less then or equal to.
     */
    LE,
    /**
     * Greater than.
     */
    GT,
    /**
     * Greater than or equal to.
     */
    GE;

}