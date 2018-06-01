package com.developersam.pl.sapl.exceptions

/**
 * [UndefinedTypeIdentifierError] reports that [badIdentifier] is not found in the current
 * environment.
 */
class UndefinedTypeIdentifierError(val badIdentifier: String)
    : CompileTimeError(reason = "Type Identifier $badIdentifier is not found.")
