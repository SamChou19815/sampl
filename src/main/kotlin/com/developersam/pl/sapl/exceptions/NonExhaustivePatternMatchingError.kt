package com.developersam.pl.sapl.exceptions

/**
 * [NonExhaustivePatternMatchingError] reports an non-exhaustive pattern matching.
 */
internal class NonExhaustivePatternMatchingError : CompileTimeError(
        reason = "Pattern matching is not exhaustive."
)
