package org.sampl.exceptions

/**
 * [CyclicDependencyError] reports cyclic dependency error in compile time.
 */
class CyclicDependencyError : CompileTimeError(reason = "Cyclic Dependency Detected!")
