package org.sampl.ast.type

import org.sampl.environment.TypeCheckingEnv
import org.sampl.exceptions.IdentifierError
import org.sampl.util.joinToGenericsInfoString

/**
 * [TypeExpr] represents a set of supported type expression in type annotation.
 */
sealed class TypeExpr {

    /**
     * [asTypeInformation] converts itself to [TypeInfo] without generics declaration.
     */
    val asTypeInformation: TypeInfo
        get() = TypeInfo(typeExpr = this)

    /**
     * [substituteGenerics] uses the given [map] to substitute generic symbols in the type
     * expression with concrete value types.
     */
    abstract fun substituteGenerics(map: Map<String, TypeExpr>): TypeExpr

    /**
     * [checkTypeValidity] tries to check the type is well-formed under the current given
     * [environment]. If not, it should throw [IdentifierError.UndefinedTypeIdentifier]
     */
    abstract fun checkTypeValidity(environment: TypeCheckingEnv)

    /**
     * [containsIdentifier] returns whether [identifier] exists in the type expression.
     */
    abstract fun containsIdentifier(identifier: String): Boolean

    /**
     * [toPrefixed] returns a new [TypeExpr] with all [typeToPrefix] prefixed with [prefix].
     */
    abstract fun toPrefixed(typeToPrefix: String, prefix: String): TypeExpr

    /**
     * [Identifier] represents a single [type] with optional [genericsInfo].
     */
    data class Identifier(
            val type: String, val genericsInfo: List<TypeExpr> = emptyList()
    ) : TypeExpr() {

        override fun substituteGenerics(map: Map<String, TypeExpr>): TypeExpr =
                map[type].takeIf { genericsInfo.isEmpty() }
                        ?: Identifier(type, genericsInfo.map { it.substituteGenerics(map) })

        override fun checkTypeValidity(environment: TypeCheckingEnv) {
            val declaredGenerics = environment.declaredTypes[type]
                    ?: throw IdentifierError.UndefinedTypeIdentifier(badIdentifier = type)
            if (declaredGenerics.size != genericsInfo.size) {
                throw IdentifierError.UndefinedTypeIdentifier(badIdentifier = type)
            }
            genericsInfo.forEach { it.checkTypeValidity(environment = environment) }
        }

        override fun containsIdentifier(identifier: String): Boolean {
            if (type == identifier) {
                return true;
            }
            return genericsInfo.any { it.containsIdentifier(identifier = identifier) }
        }

        override fun toPrefixed(typeToPrefix: String, prefix: String): TypeExpr {
            val newType = if (type.indexOf(typeToPrefix) == 0) "$prefix.$type" else type
            val newGenericsInfo = genericsInfo.map { it.toPrefixed(typeToPrefix, prefix) }
            return TypeExpr.Identifier(type = newType, genericsInfo = newGenericsInfo)
        }

        override fun toString(): String =
                if (genericsInfo.isEmpty()) type else {
                    type + genericsInfo.joinToGenericsInfoString()
                }

    }

    /**
     * [Function] represents the function type in the type annotation of the form
     * ([argumentTypes]) `->` [returnType].
     */
    data class Function(
            val argumentTypes: List<TypeExpr>, val returnType: TypeExpr
    ) : TypeExpr() {

        override fun substituteGenerics(map: Map<String, TypeExpr>): Function =
                Function(
                        argumentTypes = argumentTypes.map { it.substituteGenerics(map = map) },
                        returnType = returnType.substituteGenerics(map = map)
                )

        override fun checkTypeValidity(environment: TypeCheckingEnv) {
            argumentTypes.forEach { it.checkTypeValidity(environment = environment) }
            returnType.checkTypeValidity(environment = environment)
        }

        override fun containsIdentifier(identifier: String): Boolean {
            return returnType.containsIdentifier(identifier = identifier)
                    || argumentTypes.any { it.containsIdentifier(identifier = identifier) }
        }

        override fun toPrefixed(typeToPrefix: String, prefix: String): TypeExpr =
                TypeExpr.Function(
                        argumentTypes = argumentTypes.map { it.toPrefixed(typeToPrefix, prefix) },
                        returnType = returnType.toPrefixed(typeToPrefix, prefix)
                )

        override fun toString(): String =
                "(${argumentTypes.joinToString(separator = ", ")}) -> $returnType"

    }

}
