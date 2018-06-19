package org.sampl.exceptions

/**
 * [IdentifierError] is a collection of errors related to identifiers.
 */
sealed class IdentifierError(reason: String) : CompileTimeError(reason = reason) {

    /**
     * [ShadowedName] reports a shadowed Name of identifiers in declaration
     * during compile time.
     */
    class ShadowedName(lineNo: Int, shadowedName: String) :
            IdentifierError(reason = "Shadowed name at line $lineNo detected: $shadowedName.")

    /**
     * [UndefinedIdentifier] reports that bad identifier is not found in the current
     * environment.
     */
    class UndefinedIdentifier(lineNo: Int, badIdentifier: String) :
            IdentifierError(reason = "Identifier $badIdentifier at line $lineNo is not found.")

    /**
     * [UndefinedTypeIdentifier] reports that bad identifier is not found in the current
     * environment.
     */
    class UndefinedTypeIdentifier(lineNo: Int, badIdentifier: String) :
            CompileTimeError(reason = "Type $badIdentifier at line $lineNo is not found.")

}