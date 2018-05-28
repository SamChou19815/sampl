package com.developersam.pl.sapl.exceptions

/**
 * [CyclicDependencyError] reports cyclic dependency error in compile time.
 */
internal class CyclicDependencyError : CompileTimeError(reason = "Cyclic Dependency Detected!")
