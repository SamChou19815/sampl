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

}
