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
    class NonExhaustive internal constructor(lineNo: Int) :
            CompileTimeError(reason = "Pattern matching is not exhaustive. Check at line $lineNo.")

    /**
     * [UnmatchableType] reports an unmatchable type in pattern matching.
     */
    class UnmatchableType internal constructor(lineNo: Int, typeExpr: TypeExpr) :
            CompileTimeError(reason = "This type $typeExpr at line $lineNo is not matchable!")

    /**
     * [UnusedPattern] reports an unused pattern.
     */
    class UnusedPattern internal constructor(lineNo: Int, pattern: Pattern) :
            CompileTimeError(reason = "The pattern $pattern at line $lineNo is unused.")

    /**
     * [WrongPattern] reports a bad pattern id as a wrong pattern.
     */
    class WrongPattern internal constructor(lineNo: Int, patternId: String) :
            CompileTimeError(reason = "There is no such pattern $patternId at line $lineNo.")

}
