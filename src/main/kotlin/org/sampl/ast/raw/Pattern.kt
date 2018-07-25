package org.sampl.ast.raw

import org.sampl.ast.decorated.DecoratedPattern
import org.sampl.exceptions.PatternMatchingError
import org.sampl.ast.type.TypeExpr as T
import org.sampl.environment.TypeCheckingEnv as E

/**
 * [Pattern] is a collection of supported pattern for matching.
 */
sealed class Pattern {

    /**
     * [lineNo] reports the line number of the pattern.
     */
    abstract val lineNo: Int

    /**
     * [typeCheck] tries to type check itself with [environment] and [typeToMatch] and produces a
     * [DecoratedPattern] and a new TypeCheckingEnv after type checking this pattern.
     * It should also removed used types in [variantTypeDefs].
     */
    abstract fun typeCheck(
            typeToMatch: T, environment: E, variantTypeDefs: MutableMap<String, T?>
    ): Pair<DecoratedPattern, E>

    /**
     * [Variant] represents the variant pattern with [variantIdentifier] and potentially an
     * [associatedVariable].
     *
     * @property variantIdentifier the identifier of the variant as pattern.
     * @property associatedVariable the identifier of the optional associated variable as pattern.
     */
    data class Variant(
            override val lineNo: Int, val variantIdentifier: String,
            val associatedVariable: String?
    ) : Pattern() {

        override fun typeCheck(
                typeToMatch: T, environment: E, variantTypeDefs: MutableMap<String, T?>
        ): Pair<DecoratedPattern, E> {
            if (variantIdentifier !in variantTypeDefs) {
                throw PatternMatchingError.WrongPattern(
                        lineNo = lineNo, patternId = variantIdentifier
                )
            }
            val associatedVarType = variantTypeDefs[variantIdentifier]
            variantTypeDefs.remove(key = variantIdentifier)
            return if (associatedVariable == null && associatedVarType == null) {
                val p = DecoratedPattern.Variant(variantIdentifier = variantIdentifier)
                p to environment
            } else if (associatedVariable != null && associatedVarType != null) {
                val p = DecoratedPattern.Variant(
                        variantIdentifier = variantIdentifier,
                        associatedVariable = if (associatedVariable == "_") "_ignore" else {
                            associatedVariable
                        },
                        associatedVariableType = associatedVarType
                )
                val newE = if (associatedVariable == "_") environment else {
                    environment.copy(normalTypeEnv = environment.normalTypeEnv.put(
                            key = associatedVariable, value = associatedVarType
                    ))
                }
                p to newE
            } else {
                throw PatternMatchingError.WrongPattern(
                        lineNo = lineNo, patternId = variantIdentifier
                )
            }
        }

    }

    /**
     * [Variable] represents a variable that matches everything.
     *
     * @property identifier the identifier of the variable as pattern.
     */
    data class Variable(
            override val lineNo: Int, val identifier: String
    ) : Pattern() {

        override fun typeCheck(
                typeToMatch: T, environment: E, variantTypeDefs: MutableMap<String, T?>
        ): Pair<DecoratedPattern, E> {
            variantTypeDefs.clear()
            val p = DecoratedPattern.Variable(identifier = identifier, type = typeToMatch)
            val newE = environment.copy(normalTypeEnv = environment.normalTypeEnv.put(
                    key = identifier, value = typeToMatch
            ))
            return p to newE
        }

    }

    /**
     * [WildCard] represents a wildcard but does not bound to anything.
     */
    data class WildCard(override val lineNo: Int) : Pattern() {
        override fun typeCheck(
                typeToMatch: T, environment: E, variantTypeDefs: MutableMap<String, T?>
        ): Pair<DecoratedPattern, E> {
            variantTypeDefs.clear()
            return DecoratedPattern.WildCard to environment
        }
    }

}

