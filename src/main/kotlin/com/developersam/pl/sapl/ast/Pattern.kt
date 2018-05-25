package com.developersam.pl.sapl.ast

/**
 * [Pattern] is a collection of supported pattern for matching.
 */
sealed class Pattern

/**
 * [UnitPattern] represents the unit pattern.
 */
object UnitPattern : Pattern()

/**
 * [TuplePattern] represents the tuple pattern in the [tupleList].
 */
data class TuplePattern(val tupleList: List<Pattern>) : Pattern()

/**
 * [VariantPattern] represents the variant pattern with [variantIdentifier] and potentially an
 * [associatedPattern].
 */
data class VariantPattern(
        val variantIdentifier: String, val associatedPattern: Pattern?
) : Pattern()

/**
 * [VariablePattern] represents a variable that matches everything.
 */
data class VariablePattern(val identifier: String) : Pattern()

/**
 * [WildCardPattern] represents a wildcard but does not bound to anything.
 */
object WildCardPattern : Pattern()
