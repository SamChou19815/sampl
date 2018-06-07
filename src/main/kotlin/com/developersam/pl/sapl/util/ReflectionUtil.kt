@file:JvmName(name = "ReflectionUtil")

package com.developersam.pl.sapl.util

val <T> Class<T>.primitiveTypeName: String?
    get() = if (isPrimitive || this == String::class.java) simpleName else null
