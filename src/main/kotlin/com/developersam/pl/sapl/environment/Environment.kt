package com.developersam.pl.sapl.environment

/**
 * [Environment] defines how an environment class should behave.
 * This interface is designed to be purely functional. The implementing class should have no visible
 * side effects.
 */
interface Environment<T> {

    /**
     * [contains] checks whether the given [identifier] exists in the environment.
     */
    operator fun contains(identifier: String): Boolean

    /**
     * [get] returns the optionally existing [identifier] to the client.
     */
    operator fun get(identifier: String): T?

    /**
     * [get] returns the value associated with the [identifier] or
     * throws the exception specified in [exceptionProvider].
     */
    operator fun get(identifier: String, exceptionProvider: () -> Throwable): T

    /**
     * [set] creates a new environment with everything in this environment and a new mapping from
     * [identifier] to [value].
     */
    operator fun set(identifier: String, value: T): Environment<T>

}
