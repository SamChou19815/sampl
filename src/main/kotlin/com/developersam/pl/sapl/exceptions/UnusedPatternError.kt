package com.developersam.pl.sapl.exceptions

import com.developersam.pl.sapl.ast.raw.Pattern

/**
 * [UnusedPatternError] reports an unused [pattern].
 */
class UnusedPatternError(val pattern: Pattern)
    : CompileTimeError(reason = "The pattern $pattern is unused.")
