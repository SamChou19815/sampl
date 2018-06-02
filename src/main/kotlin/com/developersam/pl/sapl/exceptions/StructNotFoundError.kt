package com.developersam.pl.sapl.exceptions

/**
 * [StructNotFoundError] reports that a struct with [structName] is not found in current scope.
 */
class StructNotFoundError(val structName: String) : CompileTimeError(
        reason = "The given struct $structName is not found."
)
