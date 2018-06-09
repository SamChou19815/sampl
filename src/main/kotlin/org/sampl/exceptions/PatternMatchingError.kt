package org.sampl.exceptions

import org.sampl.ast.raw.Pattern
import org.sampl.ast.type.TypeExpr

/**
 * [PatternMatchingError] is a collection of errors related to pattern matching.
 */
sealed class PatternMatchingError(reason: String) : CompileTimeError(reason = reason) {

    /**
     * [NonExhaustive] reports an non-exhaustive pattern matching.
     */
    class NonExhaustive
        : CompileTimeError(reason = "Pattern matching is not exhaustive.")

    /**
     * [UnmatchableType] reports an unmatchable type [typeExpr] in pattern matching.
     */
    class UnmatchableType(val typeExpr: TypeExpr)
        : CompileTimeError(reason = "This type $typeExpr is not matchable!")

    /**
     * [UnusedPattern] reports an unused [pattern].
     */
    class UnusedPattern(val pattern: Pattern)
        : CompileTimeError(reason = "The pattern $pattern is unused.")

    /**
     * [WrongPattern] reports a bad [patternId] as a wrong pattern.
     */
    class WrongPattern(val patternId: String)
        : CompileTimeError(reason = "There is no such pattern $patternId.")

}
