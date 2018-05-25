package com.developersam.pl.sapl

/**
 * [CompileTimeError] reports the failure of compilation by giving a [message].
 */
class CompileTimeError(message: String) : RuntimeException(message)
