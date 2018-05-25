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
 * [VariantPattern] represents the variant pattern with [variantIdentifier] and potentially an
 * [associatedVariable].
 */
data class VariantPattern(
        val variantIdentifier: String, val associatedVariable: String?
) : Pattern()

/**
 * [VariablePattern] represents a variable that matches everything.
 */
data class VariablePattern(val identifier: String) : Pattern()

/**
 * [WildCardPattern] represents a wildcard but does not bound to anything.
 */
object WildCardPattern : Pattern()
