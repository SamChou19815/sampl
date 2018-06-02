package com.developersam.pl.sapl.exceptions

/**
 * [InvalidLiteralError] reports an [invalidLiteral] during compile time.
 */
class InvalidLiteralError(val invalidLiteral: String)
    : CompileTimeError(reason = "Invalid Literal Detected: $invalidLiteral.")
