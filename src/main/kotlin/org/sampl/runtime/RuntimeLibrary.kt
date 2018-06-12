package org.sampl.runtime

import org.sampl.eval.BoolValue
import org.sampl.eval.CharValue
import org.sampl.eval.FloatValue
import org.sampl.eval.IntValue
import org.sampl.eval.StringArrayValue
import org.sampl.eval.StringValue
import org.sampl.eval.UnitValue
import org.sampl.eval.Value

/**
 * [RuntimeLibrary] is a marker interface for a class that is intended to be used as a Runtime
 * library for the PL library.
 */
interface RuntimeLibrary {

    /**
     * [invokeFunction] invokes function with [name] and [arguments] and returns the result.
     */
    @Suppress(names = ["UNCHECKED_CAST"])
    fun invokeFunction(name: String, arguments: List<Value>): Value {
        val method = this.javaClass.methods
                .first { it.name == name }
        val result = method.invoke(null, *arguments.map(Value::asAny).toTypedArray())
        return when (result) {
            is Void, is Unit -> UnitValue
            is Long -> IntValue(value = result)
            is Double -> FloatValue(value = result)
            is Boolean -> BoolValue(value = result)
            is Char -> CharValue(value = result)
            is String -> StringValue(value = result)
            result.javaClass.isArray -> StringArrayValue(
                    value = result as Array<String>
            )
            else -> error(message = "Impossible")
        }
    }

    /**
     * [EmptyInstance] represents an empty instance of the [RuntimeLibrary].
     * It can be used as a default choice.
     */
    companion object EmptyInstance : RuntimeLibrary

}
