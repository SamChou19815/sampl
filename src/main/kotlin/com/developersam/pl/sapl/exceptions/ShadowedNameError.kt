package com.developersam.pl.sapl.exceptions

/**
 * [ShadowedNameError] reports a [shadowedName] of identifiers in declaration
 * during compile time.
 */
internal class ShadowedNameError(val shadowedName: String) : CompileTimeError(
        message = "Identifier Shadowing Detected: $shadowedName."
)
