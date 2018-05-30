package com.developersam.pl.sapl.exceptions

import com.developersam.pl.sapl.ast.Pattern

/**
 * [UnusedPatternError] reports an unused [pattern].
 */
internal class UnusedPatternError(val pattern: Pattern) : CompileTimeError(
        reason = "The pattern $pattern is unused."
)
