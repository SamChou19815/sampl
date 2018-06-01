package com.developersam.pl.sapl.exceptions

/**
 * [TooManyArgumentsError] reports the problem of too many arguments in function application.
 */
class TooManyArgumentsError
    : CompileTimeError(reason = "Too many arguments in function application.")
