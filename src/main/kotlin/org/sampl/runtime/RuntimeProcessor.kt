package org.sampl.runtime

import org.sampl.ast.type.TypeExpr
import org.sampl.ast.type.boolTypeExpr
import org.sampl.ast.type.charTypeExpr
import org.sampl.ast.type.floatTypeExpr
import org.sampl.ast.type.intTypeExpr
import org.sampl.ast.type.stringTypeExpr
import org.sampl.ast.type.unitTypeExpr
import org.sampl.exceptions.DisallowedRuntimeFunctionError
import java.lang.reflect.Method
import java.lang.reflect.Modifier

/**
 * [RuntimeProcessor] provides functions to process the given runtime library.
 */
internal object RuntimeProcessor {

    /**
     * [toAnnotatedMethods] converts the library instance to a list of pairs of the form
     * (methods name, method function type)
     */
    @JvmStatic
    fun RuntimeLibrary.toAnnotatedMethods(): List<Pair<String, TypeExpr>> =
            this::class.java.toAnnotatedMethods()

    /**
     * [toAnnotatedMethods] converts the library class to a list of pairs of the form
     * (methods name, method function type)
     */
    @JvmStatic
    fun <T : RuntimeLibrary> Class<out T>.toAnnotatedMethods(): List<Pair<String, TypeExpr>> =
            methods.asSequence()
                    .filter { Modifier.isStatic(it.modifiers) }
                    .filter { it.getAnnotation(RuntimeFunction::class.java) != null }
                    .map { it.name to it.toTypeExpr() }
                    .toList()

    /**
     * [Method.toTypeExpr] converts the method to an equivalent [TypeExpr] in this programming
     * language if possible.
     * If it is impossible due to some rules, it will throw [DisallowedRuntimeFunctionError].
     */
    @JvmStatic
    private fun Method.toTypeExpr(): TypeExpr {
        val parameterTypes = this.parameterTypes
                .asSequence()
                .map { it.toPredefinedTypeExpr() }
                .filterNotNull()
                .toList()
        if (parameterTypes.size != this.parameterTypes.size) {
            throw DisallowedRuntimeFunctionError()
        }
        val returnType = this.returnType.toPredefinedTypeExpr()
                ?: throw DisallowedRuntimeFunctionError()
        return TypeExpr.Function(parameterTypes, returnType)
    }

    /**
     * [toPredefinedTypeExpr] returns the equivalent predefined type expression in this
     * programming language for this class.
     * If there is no such correspondence, `null` will be returned.
     */
    @JvmStatic
    private fun <T : Any> Class<T>.toPredefinedTypeExpr(): TypeExpr? = when (simpleName) {
        "void" -> unitTypeExpr
        "long" -> intTypeExpr
        "double" -> floatTypeExpr
        "boolean" -> boolTypeExpr
        "char" -> charTypeExpr
        "String" -> stringTypeExpr
        else -> null
    }

}
