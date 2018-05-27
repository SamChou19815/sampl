package com.developersam.pl.sapl.environment

import fj.Ord
import fj.data.TreeMap

/**
 * [FunctionalEnvironment] is an functional implementation of the [Environment] interface.
 *
 * @param map map is the backing field of the environment.
 */
internal class FunctionalEnvironment<T> private constructor(
        private val map: TreeMap<String, T>
) : Environment<T> {

    override fun contains(identifier: String): Boolean = map.contains(identifier)

    @Suppress(names = ["UNCHECKED_CAST"])
    override fun get(identifier: String): T? {
        val valueOpt = map[identifier]
        return if (valueOpt.isNone) null else valueOpt.some()
    }

    override fun get(identifier: String, exceptionProvider: () -> Throwable): T {
        val valueOpt = map[identifier]
        return if (valueOpt.isNone) throw exceptionProvider() else valueOpt.some()
    }

    override fun set(identifier: String, value: T): Environment<T> =
            FunctionalEnvironment(map = map.set(identifier, value))

    companion object {
        /**
         * [emptyEnvironment] is the singleton empty environment.
         */
        private val emptyEnvironment: Environment<Any> =
                FunctionalEnvironment(map = TreeMap.empty(Ord.stringOrd))

        /**
         * [getEmpty] returns the singleton empty environment.
         */
        @Suppress(names = ["UNCHECKED_CAST"])
        fun <T> getEmpty(): Environment<T> = emptyEnvironment as Environment<T>
    }

}
