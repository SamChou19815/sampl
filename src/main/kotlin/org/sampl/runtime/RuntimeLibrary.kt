package org.sampl.runtime

/**
 * [RuntimeLibrary] is a marker interface for a class that is intended to be used as a Runtime
 * library for the PL library.
 */
interface RuntimeLibrary {

    /**
     * [EmptyInstance] represents an empty instance of the [RuntimeLibrary].
     * It can be used as a default choice.
     */
    companion object EmptyInstance : RuntimeLibrary

}
