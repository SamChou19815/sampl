package org.sampl.ast.common

import org.sampl.ast.type.TypeExpr
import org.sampl.ast.type.boolTypeExpr
import org.sampl.ast.type.charTypeExpr
import org.sampl.ast.type.floatTypeExpr
import org.sampl.ast.type.intTypeExpr
import org.sampl.ast.type.stringTypeExpr
import org.sampl.ast.type.unitTypeExpr

/**
 * [Literal] represents a set of supported literal.
 *
 * @property inferredType the inferred type from the literal.
 */
internal sealed class Literal(val inferredType: TypeExpr) {

    /**
     * [Unit] is the literal for unit.
     */
    object Unit : Literal(inferredType = unitTypeExpr) {

        /**
         * Returns the string representation of unit.
         */
        override fun toString(): kotlin.String = "()"

    }

    /**
     * [Int] is the literal for int with [value].
     *
     * @property value value of the literal.
     */
    data class Int(val value: Long) : Literal(inferredType = intTypeExpr) {

        /**
         * Returns the string representation of this int.
         */
        override fun toString(): kotlin.String = value.toString()

    }

    /**
     * [Float] is the literal for float with [value].
     *
     * @property value value of the literal.
     */
    data class Float(val value: Double) : Literal(inferredType = floatTypeExpr) {

        /**
         * Returns the string representation of this float.
         */
        override fun toString(): kotlin.String = value.toString()

    }

    /**
     * [Bool] is the literal for bool with [value].
     *
     * @property value value of the literal.
     */
    data class Bool(val value: Boolean) : Literal(inferredType = boolTypeExpr) {

        /**
         * Returns the string representation of this bool.
         */
        override fun toString(): kotlin.String = value.toString()

    }

    /**
     * [Char] is the literal for char with [value].
     *
     * @property value value of the literal.
     */
    data class Char(val value: kotlin.Char) : Literal(inferredType = charTypeExpr) {

        /**
         * Returns the string representation of this char.
         */
        override fun toString(): kotlin.String = "'$value'"

    }

    /**
     * [String] is the literal for string with [value].
     *
     * @property value value of the literal.
     */
    data class String(val value: kotlin.String) : Literal(inferredType = stringTypeExpr) {

        /**
         * Returns the string representation of this string.
         */
        override fun toString(): kotlin.String = "\"$value\""

    }

}
