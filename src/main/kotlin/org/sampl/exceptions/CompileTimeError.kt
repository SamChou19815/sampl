package org.sampl.exceptions

/**
 * [CompileTimeError] reports the failure of compilation by giving a reason.
 */
open class CompileTimeError(reason: String) : RuntimeException(reason)
