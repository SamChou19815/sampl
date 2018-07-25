package org.sampl.eval

import org.sampl.ast.common.FunctionCategory
import org.sampl.ast.decorated.DecoratedExpression
import org.sampl.codegen.PrettyPrinter
import org.sampl.environment.EvalEnv
import java.util.Arrays

/**
 * [Value] defines a set of supported values during interpretation.
 */
sealed class Value {

    /**
     * [asAny] turns the value into an [Any] object.
     */
    internal abstract val asAny: Any

    /**
     * [toString] returns the string representation of the value.
     */
    abstract override fun toString(): String

}

/*
 * ------------------------------------------------------------
 * Part 1: Primitive Values
 * ------------------------------------------------------------
 */

/**
 * [UnitValue] represents the unit value.
 */
object UnitValue : Value() {

    /**
     * Returns the value of the object as any.
     */
    override val asAny: Any get() = Unit

    /**
     * [toString] returns the string representation of the unit value.
     */
    override fun toString(): String = "Unit"

}

/**
 * [IntValue] represents an int value with actual [value].
 *
 * @property value the actual value wrapped.
 */
data class IntValue(val value: Long) : Value() {

    /**
     * Returns the value of the object as any.
     */
    override val asAny: Any get() = value

    /**
     * [toString] returns the string representation of the int value.
     */
    override fun toString(): String = value.toString()

}

/**
 * [FloatValue] represents a float value with actual [value].
 *
 * @property value the actual value wrapped.
 */
data class FloatValue(val value: Double) : Value() {

    /**
     * Returns the value of the object as any.
     */
    override val asAny: Any get() = value

    /**
     * [toString] returns the string representation of the float value.
     */
    override fun toString(): String = value.toString()

}

/**
 * [BoolValue] represents a bool value with actual [value].
 *
 * @property value the actual value wrapped.
 */
data class BoolValue(val value: Boolean) : Value() {

    /**
     * Returns the value of the object as any.
     */
    override val asAny: Any get() = value

    /**
     * [toString] returns the string representation of the bool value.
     */
    override fun toString(): String = value.toString()

}

/**
 * [CharValue] represents a char value with actual [value].
 *
 * @property value the actual value wrapped.
 */
data class CharValue(val value: Char) : Value() {

    /**
     * Returns the value of the object as any.
     */
    override val asAny: Any get() = value

    /**
     * [toString] returns the string representation of the char value.
     */
    override fun toString(): String = "'$value'"

}

/**
 * [StringValue] represents a string value with actual [value].
 *
 * @property value the actual value wrapped.
 */
data class StringValue(val value: String) : Value() {

    /**
     * Returns the value of the object as any.
     */
    override val asAny: Any get() = value

    /**
     * [toString] returns the string representation of the string value.
     */
    override fun toString(): String = "\"$value\""

}

/**
 * [StringArrayValue] represents a string array value with actual [value].
 *
 * @property value the actual value wrapped.
 */
data class StringArrayValue(val value: Array<String>) : Value() {

    /**
     * Returns the value of the object as any.
     */
    override val asAny: Any get() = Arrays.toString(value)

    /**
     * [toString] returns the string representation of the string array value.
     */
    override fun toString(): String = Arrays.toString(value)

    /**
     * [equals] returns whether this value equals [other] object.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as StringArrayValue
        return Arrays.equals(value, other.value)
    }

    /**
     * [hashCode] returns the hashcode of the value.
     */
    override fun hashCode(): Int = Arrays.hashCode(value)

}

/*
 * ------------------------------------------------------------
 * Part 2: Class Values
 * ------------------------------------------------------------
 */

/**
 * [VariantValue] represents a variant with [variantIdentifier] and a potential [associatedValue].
 *
 * @property variantIdentifier the identifier of the variant.
 * @property associatedValue the optional value associated with the variant.
 */
data class VariantValue(val variantIdentifier: String, val associatedValue: Value?) : Value() {

    /**
     * Returns the value of the object as any.
     */
    override val asAny: Any get() = this

    /**
     * [toString] returns the string representation of the variant value.
     */
    override fun toString(): String =
            "<Variant: " + if (associatedValue == null) {
                variantIdentifier
            } else {
                "$variantIdentifier($associatedValue)"
            } + ">"

}

/**
 * [StructValue] represents a struct with a [nameValueMap].
 *
 * @property nameValueMap the map from name to values for a struct.
 */
data class StructValue(val nameValueMap: Map<String, Value>) : Value() {

    /**
     * Returns the value of the object as any.
     */
    override val asAny: Any get() = nameValueMap

    /**
     * [toString] returns the string representation of the struct value.
     */
    override fun toString(): String = nameValueMap.toString()

}

/*
 * ------------------------------------------------------------
 * Part 3: Function Values
 * ------------------------------------------------------------
 */

/**
 * [ClosureValue] is a function closure of [arguments], function [code] and the [environment].
 * It should also reports the function [category] with an optional [name] (required for
 * non-user-defined functions).
 *
 * @property category category of the closure.
 * @property name name of the closure, which is optional.
 * @property environment environment of the closure.
 * @property arguments argument declaration of the closure.
 * @property code body of the closure.
 */
class ClosureValue internal constructor(
        val category: FunctionCategory, val name: String? = null, var environment: EvalEnv,
        val arguments: List<String>, val code: DecoratedExpression
) : Value() {

    /**
     * Returns the value of the object as any.
     */
    override val asAny: Any get() = this

    /**
     * [toString] returns the string representation of the closure value.
     */
    override fun toString(): String =
            "<fun>($arguments) :=\n${PrettyPrinter.prettyPrint(node = code)}"

}
