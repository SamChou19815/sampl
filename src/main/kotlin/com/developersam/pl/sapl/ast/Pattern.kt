package com.developersam.pl.sapl.ast

/**
 * [Pattern] is a collection of supported pattern for matching.
 */
internal sealed class Pattern

/**
 * [VariantPattern] represents the variant pattern with [variantIdentifier] and potentially an
 * [associatedVariable].
 */
internal data class VariantPattern(
        val variantIdentifier: String, val associatedVariable: String?
) : Pattern()

/**
 * [VariablePattern] represents a variable that matches everything.
 */
internal data class VariablePattern(val identifier: String) : Pattern()

/**
 * [WildCardPattern] represents a wildcard but does not bound to anything.
 */
internal object WildCardPattern : Pattern()
