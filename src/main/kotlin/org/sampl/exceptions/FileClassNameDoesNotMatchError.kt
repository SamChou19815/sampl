package org.sampl.exceptions

/**
 * [FileClassNameDoesNotMatchError] reports the problem of [filename] and [className] does not
 * matching problem.
 */
class FileClassNameDoesNotMatchError(val filename: String, val className: String)
    : CompileTimeError(reason = "Filename $filename and class name $className does not match!")