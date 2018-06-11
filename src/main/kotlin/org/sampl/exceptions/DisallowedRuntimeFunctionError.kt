package org.sampl.exceptions

/**
 * [DisallowedRuntimeFunctionError] reports the problem of using non-primitive-or-string types.
 */
class DisallowedRuntimeFunctionError :
        CompileTimeError(reason = "Runtime functions can only contain primitive types and string.")
