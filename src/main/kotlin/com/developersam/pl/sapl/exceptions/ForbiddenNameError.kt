package com.developersam.pl.sapl.exceptions

/**
 * [ForbiddenNameError] reports the usage of an forbidden name in the program.
 */
class ForbiddenNameError(val name: String): CompileTimeError(reason = "Forbidden name used: $name")
