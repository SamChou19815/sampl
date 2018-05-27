package com.developersam.pl.sapl.exceptions

/**
 * [CompileTimeError] reports the failure of compilation by giving a [message].
 */
internal open class CompileTimeError(message: String) : RuntimeException(message = message)
