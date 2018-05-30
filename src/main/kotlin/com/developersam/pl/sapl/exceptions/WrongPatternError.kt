package com.developersam.pl.sapl.exceptions

/**
 * [WrongPatternError] reports a bad [patternId] as a wrong pattern.
 */
internal class WrongPatternError(val patternId: String) : CompileTimeError(
        reason = "There is no such pattern $patternId."
)
