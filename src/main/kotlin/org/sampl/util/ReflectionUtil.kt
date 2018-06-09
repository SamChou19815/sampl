@file:JvmName(name = "ReflectionUtil")

package org.sampl.util

val <T> Class<T>.primitiveTypeName: String?
    get() = if (isPrimitive || this == String::class.java) simpleName else null
