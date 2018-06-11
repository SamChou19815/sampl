package org.sampl.exceptions

/**
 * [IdentifierError] is a collection of errors related to identifiers.
 */
sealed class IdentifierError(reason: String) : CompileTimeError(reason = reason) {

    /**
     * [ForbiddenName] reports the usage of an forbidden name in the program.
     */
    class ForbiddenName(val name: String) :
            IdentifierError(reason = "Forbidden name used: $name")

    /**
     * [ShadowedName] reports a [shadowedName] of identifiers in declaration
     * during compile time.
     */
    class ShadowedName(val shadowedName: String) :
            IdentifierError(reason = "Identifier Shadowing Detected: $shadowedName.")

    /**
     * [UndefinedIdentifier] reports that [badIdentifier] is not found in the current
     * environment.
     */
    class UndefinedIdentifier(val badIdentifier: String) :
            IdentifierError(reason = "Identifier $badIdentifier is not found.")

    /**
     * [UndefinedTypeIdentifier] reports that [badIdentifier] is not found in the current
     * environment.
     */
    class UndefinedTypeIdentifier(val badIdentifier: String) :
            CompileTimeError(reason = "Type Identifier $badIdentifier is not found.")

}