package com.developersam.pl.sapl.runtime

import com.developersam.pl.sapl.ast.type.TypeExpr
import com.developersam.pl.sapl.ast.type.boolTypeExpr
import com.developersam.pl.sapl.ast.type.charTypeExpr
import com.developersam.pl.sapl.ast.type.floatTypeExpr
import com.developersam.pl.sapl.ast.type.intTypeExpr
import com.developersam.pl.sapl.ast.type.stringTypeExpr
import com.developersam.pl.sapl.ast.type.unitTypeExpr
import com.developersam.pl.sapl.exceptions.DisallowedRuntimeFunctionError
import com.developersam.pl.sapl.util.primitiveTypeName
import java.lang.reflect.Method

class RuntimeProcessor(private val libraryInstance: RuntimeLibrary) {

    fun process() {
        val runtimeFunctions: List<Method> = libraryInstance.javaClass.methods
                .asSequence()
                .filter { it.getAnnotation(RuntimeFunction::class.java) != null }
                .toList()
        for (method in runtimeFunctions) {
            val parameterTypeStrings = method.parameterTypes
                    .asSequence()
                    .map { it.primitiveTypeName }
                    .filterNotNull()
                    .toList()
            if (parameterTypeStrings.size != method.parameterTypes.size) {
                throw DisallowedRuntimeFunctionError()
            }
            val returnTypeString = method.returnType.primitiveTypeName
                    ?: throw DisallowedRuntimeFunctionError()
            val typeExpr = stringTypesToExpr(
                    parameters = parameterTypeStrings, returnType = returnTypeString
            )
            // Assume code to be correct.
            val code = method.getAnnotation(RuntimeFunction::class.java).code
        }
    }

    /**
     * [stringTypeExpr] converts a [typeName] in string to the format in [TypeExpr.Identifier].
     */
    private fun stringTypeToExpr(typeName: String): TypeExpr.Identifier {
        return when (typeName) {
            "void" -> unitTypeExpr
            "int", "long" -> intTypeExpr
            "float", "double" -> floatTypeExpr
            "boolean" -> boolTypeExpr
            "char" -> charTypeExpr
            "String" -> stringTypeExpr
            else -> error(message = "Impossible")
        }
    }

    /**
     * [stringTypeExpr] converts [parameters] and a [returnType] in string to the format
     * in function [TypeExpr.Function].
     */
    private fun stringTypesToExpr(parameters: List<String>, returnType: String): TypeExpr.Function =
            TypeExpr.Function(
                    argumentTypes = parameters.map(::stringTypeToExpr),
                    returnType = stringTypeToExpr(returnType)
            )

}
