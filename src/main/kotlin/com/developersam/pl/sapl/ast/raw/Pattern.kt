package com.developersam.pl.sapl.ast.raw

import com.developersam.pl.sapl.ast.type.TypeExpr as T
import com.developersam.pl.sapl.ast.decorated.DecoratedPattern
import com.developersam.pl.sapl.exceptions.WrongPatternError
import com.developersam.pl.sapl.environment.TypeCheckingEnv as E

/**
 * [Pattern] is a collection of supported pattern for matching.
 */
sealed class Pattern {

    /**
     * [typeCheck] tries to type check itself with [environment] and [typeToMatch] and produces a
     * [DecoratedPattern] and a new TypeCheckingEnv after type checking this pattern.
     * It should also removed used types in [variantTypeDefs].
     */
    abstract fun typeCheck(typeToMatch: T, environment: E,
                           variantTypeDefs: MutableMap<String, T?>): Pair<DecoratedPattern, E>

    /**
     * [Variant] represents the variant pattern with [variantIdentifier] and potentially an
     * [associatedVariable].
     */
    data class Variant(val variantIdentifier: String, val associatedVariable: String?) : Pattern() {

        override fun typeCheck(typeToMatch: T, environment: E,
                               variantTypeDefs: MutableMap<String, T?>): Pair<DecoratedPattern, E> {
            if (variantIdentifier !in variantTypeDefs) {
                throw WrongPatternError(patternId = variantIdentifier)
            }
            variantTypeDefs.remove(key = variantIdentifier)
            val associatedVarType = variantTypeDefs[variantIdentifier]
            return if (associatedVariable == null && associatedVarType == null) {
                val p = DecoratedPattern.Variant(variantIdentifier = variantIdentifier)
                p to environment
            } else if (associatedVariable != null && associatedVarType != null) {
                val p = DecoratedPattern.Variant(
                        variantIdentifier = variantIdentifier,
                        associatedVariable = associatedVariable,
                        associatedVariableType = associatedVarType
                )
                val newE = environment.put(
                        variable = associatedVariable,
                        typeInfo = associatedVarType.asTypeInformation
                )
                p to newE
            } else {
                throw WrongPatternError(patternId = variantIdentifier)
            }
        }

    }

    /**
     * [Variable] represents a variable that matches everything.
     */
    data class Variable(val identifier: String) : Pattern() {

        override fun typeCheck(typeToMatch: T, environment: E,
                               variantTypeDefs: MutableMap<String, T?>): Pair<DecoratedPattern, E> {
            variantTypeDefs.clear()
            val p = DecoratedPattern.Variable(
                    identifier = identifier, type = typeToMatch
            )
            val newE = environment.put(
                    variable = identifier, typeInfo = typeToMatch.asTypeInformation
            )
            return p to newE
        }

    }

    /**
     * [WildCard] represents a wildcard but does not bound to anything.
     */
    object WildCard : Pattern() {
        override fun typeCheck(typeToMatch: T, environment: E,
                               variantTypeDefs: MutableMap<String, T?>): Pair<DecoratedPattern, E> {
            variantTypeDefs.clear()
            return DecoratedPattern.WildCard to environment
        }
    }

}

