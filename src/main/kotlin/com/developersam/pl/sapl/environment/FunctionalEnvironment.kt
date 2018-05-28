package com.developersam.pl.sapl.environment

import com.developersam.fp.FpMap

/**
 * [FunctionalEnvironment] is an functional implementation of the [Environment] interface.
 *
 * @param map map is the backing field of the environment.
 */
internal class FunctionalEnvironment<T> private constructor(
        private val map: FpMap<String, T>
) : Environment<T> {

    override fun contains(identifier: String): Boolean = map.contains(identifier)

    @Suppress(names = ["UNCHECKED_CAST"])
    override fun get(identifier: String): T? = map[identifier]

    override fun get(identifier: String, exceptionProvider: () -> Throwable): T =
            map[identifier] ?: throw exceptionProvider()

    override fun set(identifier: String, value: T): Environment<T> =
            FunctionalEnvironment(map = map.put(key = identifier, value = value))

    companion object {
        /**
         * [emptyEnvironment] is the singleton empty environment.
         */
        private val emptyEnvironment: Environment<Any> =
                FunctionalEnvironment(map = FpMap.empty())

        /**
         * [getEmpty] returns the singleton empty environment.
         */
        @Suppress(names = ["UNCHECKED_CAST"])
        fun <T> getEmpty(): Environment<T> = emptyEnvironment as Environment<T>
    }

}
