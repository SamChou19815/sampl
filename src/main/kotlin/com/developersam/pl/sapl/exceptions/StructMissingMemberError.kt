package com.developersam.pl.sapl.exceptions

/**
 * [StructMissingMemberError] reports the error when struct declaration of [structName] misses a
 * member [missingMember].
 */
class StructMissingMemberError(
        val structName: String, val missingMember: String
) : CompileTimeError(reason = "Missing member in $structName: $missingMember.")
