package com.developersam.pl.sapl.exceptions

/**
 * [StructError] is a collection of errors related to structs.
 */
sealed class StructError(reason: String) : CompileTimeError(reason = reason) {

    /**
     * [MissingMember] reports the error when struct declaration of [structName] misses a
     * member [missingMember].
     */
    class MissingMember(val structName: String, val missingMember: String)
        : StructError(reason = "Missing member in $structName: $missingMember.")

    /**
     * [NoSuchMember] reports the error when the user tries to use a [memberName] not found
     * in [structName].
     */
    class NoSuchMember(val structName: String, val memberName: String)
        : StructError(reason = "Member $memberName is not found in $structName." )

    /**
     * [NotFound] reports that a struct with [structName] is not found in current scope.
     */
    class NotFound(val structName: String)
        : StructError(reason = "The given struct $structName is not found.")

}
