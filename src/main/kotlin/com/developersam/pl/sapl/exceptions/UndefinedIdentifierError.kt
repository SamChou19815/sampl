package com.developersam.pl.sapl.exceptions

/**
 * [UndefinedIdentifierError] reports that [badIdentifier] is not found in the current environment.
 */
internal class UndefinedIdentifierError(val badIdentifier: String) : CompileTimeError(
        reason = "Identifier $badIdentifier is not found."
)
