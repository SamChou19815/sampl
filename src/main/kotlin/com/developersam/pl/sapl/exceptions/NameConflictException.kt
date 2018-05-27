package com.developersam.pl.sapl.exceptions

/**
 * [NameConflictException] reports a [conflictedName] of identifiers in declaration
 * during compile time.
 */
class NameConflictException(val conflictedName: String) : CompileTimeError(
        message = "Identifier Conflict Detected: $conflictedName."
)
