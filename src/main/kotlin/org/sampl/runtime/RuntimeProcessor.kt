package org.sampl.runtime

import org.sampl.ast.type.TypeExpr
import org.sampl.ast.type.boolTypeExpr
import org.sampl.ast.type.charTypeExpr
import org.sampl.ast.type.floatTypeExpr
import org.sampl.ast.type.intTypeExpr
import org.sampl.ast.type.stringTypeExpr
import org.sampl.ast.type.unitTypeExpr
import org.sampl.exceptions.DisallowedRuntimeFunctionError
import org.sampl.util.primitiveTypeName
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
     * [stringTypesToExpr] converts [parameters] and a [returnType] in string to the format
     * in function [TypeExpr.Function].
     */
    private fun stringTypesToExpr(parameters: List<String>, returnType: String): TypeExpr.Function =
            TypeExpr.Function(
                    argumentTypes = parameters.map(::stringTypeToExpr),
                    returnType = stringTypeToExpr(returnType)
            )

}
