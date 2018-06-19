package org.sampl.exceptions

/**
 * [StructError] is a collection of errors related to structs.
 */
sealed class StructError(reason: String) : CompileTimeError(reason = reason) {

    /**
     * [MissingMember] reports the error when struct declaration of the given struct name misses a
     * member.
     */
    class MissingMember(lineNo: Int, structName: String, missingMember: String) :
            StructError(reason = "Missing member in $structName: $missingMember at $lineNo.")

    /**
     * [NoSuchMember] reports the error when the user tries to use a member name not found
     * in the given struct name.
     */
    class NoSuchMember(lineNo: Int, structName: String, memberName: String) :
            StructError(reason = "Member $memberName is not found in $structName at line $lineNo.")

    /**
     * [NotFound] reports that a struct with the given struct name is not found in current scope.
     */
    class NotFound(lineNo: Int, structName: String) :
            StructError(reason = "The given struct $structName at line $lineNo is not found.")

}
