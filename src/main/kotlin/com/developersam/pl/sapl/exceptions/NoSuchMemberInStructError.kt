package com.developersam.pl.sapl.exceptions

/**
 * [NoSuchMemberInStructError] reports the error when the user tries to use a [memberName] not found
 * in [structName].
 */
class NoSuchMemberInStructError(val structName: String, val memberName: String) : CompileTimeError(
        reason = "Member $memberName is not found in $structName."
)
