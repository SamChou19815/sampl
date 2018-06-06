package com.developersam.pl.sapl.runtime

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
/**
 * [RuntimeLibrary] is the marker annotation for the interpreter to detect and validate
 * runtime class library.
 */
annotation class RuntimeLibrary
