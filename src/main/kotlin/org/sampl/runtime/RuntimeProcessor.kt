package org.sampl.runtime

import org.sampl.ast.common.FunctionCategory
import org.sampl.ast.common.Literal
import org.sampl.ast.raw.ClassFunctionMember
import org.sampl.ast.raw.Clazz
import org.sampl.ast.raw.LiteralExpr
import org.sampl.ast.type.TypeExpr
import org.sampl.ast.type.TypeInfo
import org.sampl.ast.type.boolTypeExpr
import org.sampl.ast.type.charTypeExpr
import org.sampl.ast.type.floatTypeExpr
import org.sampl.ast.type.intTypeExpr
import org.sampl.ast.type.stringTypeExpr
import org.sampl.ast.type.unitTypeExpr
import org.sampl.exceptions.DisallowedRuntimeFunctionError
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.lang.reflect.Type
import org.sampl.runtime.RuntimeLibrary as R

/**
 * [toAllowedTypeExpr] returns the equivalent allowed type expression in this
 * programming language for this class. It will also consider the generic declarations from
 * [genericsInfo].
 * If there is no such correspondence, `null` will be returned.
 */
private fun Type.toAllowedTypeExpr(genericsInfo: List<String>): TypeExpr? = when (typeName) {
    "void" -> unitTypeExpr
    "long" -> intTypeExpr
    "double" -> floatTypeExpr
    "boolean" -> boolTypeExpr
    "char" -> charTypeExpr
    "java.lang.String" -> stringTypeExpr
    in genericsInfo -> TypeExpr.Identifier(type = typeName)
    else -> {
        println(typeName); null
    }
}

/**
 * [Method.toTypeInfo] converts the method to an equivalent [TypeExpr] in this programming
 * language if possible.
 * It has a parameter [allowGenerics] which defaults to false. It can be used to control whether
 * to allow generics in runtime library.
 * If it is impossible due to some rules, it will throw [DisallowedRuntimeFunctionError].
 */
private fun Method.toTypeInfo(allowGenerics: Boolean): TypeInfo {
    if (!allowGenerics && typeParameters.isNotEmpty()) {
        throw DisallowedRuntimeFunctionError()
    }
    val genericsInfo = typeParameters.map { it.typeName }
    val parameterTypes = genericParameterTypes
            .asSequence()
            .map { it.toAllowedTypeExpr(genericsInfo) }
            .filterNotNull()
            .toList()
    if (parameterTypes.size != genericParameterTypes.size) {
        throw DisallowedRuntimeFunctionError()
    }
    val returnType = this.returnType.toAllowedTypeExpr(genericsInfo = genericsInfo)
            ?: throw DisallowedRuntimeFunctionError()
    val functionType = TypeExpr.Function(argumentTypes = parameterTypes, returnType = returnType)
    return TypeInfo(typeExpr = functionType, genericsInfo = genericsInfo)
}

/**
 * [toAnnotatedFunctionSequence] converts the library instance to a sequence of pairs of the form
 * (methods name, method function type).
 * It has a parameter [allowGenerics] which defaults to false. It can be used to control whether
 * to allow generics in runtime library.
 */
private fun R.toAnnotatedFunctionSequence(
        allowGenerics: Boolean = false
): Sequence<Pair<String, TypeInfo>> =
        this::class.java.methods.asSequence()
                .filter { Modifier.isStatic(it.modifiers) }
                .filter { it.getAnnotation(RuntimeFunction::class.java) != null }
                .map { it.name to it.toTypeInfo(allowGenerics = allowGenerics) }

/**
 * [toAnnotatedFunctions] converts the library instance to a list of pairs of the form
 * (methods name, method function type).
 * It has a parameter [allowGenerics] which defaults to false. It can be used to control whether
 * to allow generics in runtime library.
 */
internal fun R.toAnnotatedFunctions(allowGenerics: Boolean = false): List<Pair<String, TypeInfo>> =
        toAnnotatedFunctionSequence(allowGenerics = allowGenerics).toList()

/**
 * [toFunctionMember] converts a pair of function name and type info to a class function member
 * with the specified function category [c].
 */
private fun Pair<String, TypeInfo>.toFunctionMember(c: FunctionCategory): ClassFunctionMember {
    val (name, typeInfo) = this
    val functionType = typeInfo.typeExpr as TypeExpr.Function
    val arguments = functionType.argumentTypes.mapIndexed { i, t -> "var$i" to t }
    return ClassFunctionMember(
            category = c, isPublic = true, identifier = name,
            genericsDeclaration = typeInfo.genericsInfo,
            arguments = arguments, returnType = functionType.returnType,
            body = LiteralExpr(literal = Literal.Unit) // dummy expression
    )
}

/**
 * [Clazz.withInjectedRuntime] returns a copy of itself but with [PrimitiveRuntimeLibrary]
 * and an optional [providedRuntimeLibrary] injected to itself.
 */
internal fun Clazz.withInjectedRuntime(providedRuntimeLibrary: R? = null): Clazz {
    val primitiveRTSeq = PrimitiveRuntimeLibrary
            .toAnnotatedFunctionSequence(allowGenerics = true)
            .map { it.toFunctionMember(c = FunctionCategory.PRIMITIVE) }
    val providedRTSeq = providedRuntimeLibrary
            ?.toAnnotatedFunctionSequence()
            ?.map { it.toFunctionMember(c = FunctionCategory.PROVIDED) }
            ?: emptySequence()
    val newFunctionMembers = primitiveRTSeq.toMutableList()
            .apply { addAll(elements = providedRTSeq) }
            .apply { addAll(elements = members.functionMembers) }
            .toList()
    return copy(members = members.copy(functionMembers = newFunctionMembers))
}
