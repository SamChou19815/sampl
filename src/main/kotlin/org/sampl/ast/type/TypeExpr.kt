package org.sampl.ast.type

import org.sampl.environment.TypeCheckingEnv
import org.sampl.exceptions.IdentifierError
import org.sampl.util.joinToGenericsInfoString

/**
 * [TypeExpr] represents a set of supported type expression in type annotation.
 */
sealed class TypeExpr {

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
     * [Identifier] represents a single [type] with optional [genericsInfo], where [type] is at
     * [lineNo]. If [lineNo] does not make sense (generated types), it is defaulted to -1.
     *
     * @property lineNo line number where this type appears.
     * @property type nominal type of the identifier.
     * @property genericsInfo a list of associated generics info, if any.
     */
    data class Identifier(
            val lineNo: Int = -1, val type: String, val genericsInfo: List<TypeExpr> = emptyList()
    ) : TypeExpr() {

        /**
         * @see [TypeExpr.substituteGenerics]
         */
        override fun substituteGenerics(map: Map<String, TypeExpr>): TypeExpr =
                map[type].takeIf { genericsInfo.isEmpty() }
                        ?: Identifier(lineNo, type, genericsInfo.map { it.substituteGenerics(map) })

        /**
         * @see [TypeExpr.checkTypeValidity]
         */
        override fun checkTypeValidity(environment: TypeCheckingEnv) {
            val declaredGenerics = environment.declaredTypes[type]
                    ?: throw IdentifierError.UndefinedTypeIdentifier(lineNo, type)
            if (declaredGenerics.size != genericsInfo.size) {
                throw IdentifierError.UndefinedTypeIdentifier(lineNo, type)
            }
            genericsInfo.forEach { it.checkTypeValidity(environment = environment) }
        }

        /**
         * @see [TypeExpr.containsIdentifier]
         */
        override fun containsIdentifier(identifier: String): Boolean =
                type == identifier || genericsInfo.any { it.containsIdentifier(identifier) }

        /**
         * @see [TypeExpr.toPrefixed]
         */
        override fun toPrefixed(typeToPrefix: String, prefix: String): TypeExpr {
            val newType = if (type.indexOf(typeToPrefix) == 0) "$prefix.$type" else type
            val newGenericsInfo = genericsInfo.map { it.toPrefixed(typeToPrefix, prefix) }
            return TypeExpr.Identifier(
                    lineNo = lineNo, type = newType, genericsInfo = newGenericsInfo
            )
        }

        /**
         * [toString] returns the string representation of the type.
         */
        override fun toString(): String =
                if (genericsInfo.isEmpty()) type else {
                    type + genericsInfo.joinToGenericsInfoString()
                }

        /**
         * [equals] checks whether this type is equal to [other] object.
         */
        override fun equals(other: Any?): Boolean {
            if (this === other) {
                return true
            }
            if (other is TypeExpr.Identifier) {
                return type == other.type && genericsInfo == other.genericsInfo
            }
            return false
        }

        /**
         * [hashCode] returns the hashcode the type.
         */
        override fun hashCode(): Int = type.hashCode() * 31 + genericsInfo.hashCode()

    }

    /**
     * [Function] represents the function type in the type annotation of the form
     * ([argumentTypes]) `->` [returnType].
     *
     * @property argumentTypes a list of types of arguments.
     * @property returnType type of the return value.
     */
    data class Function(
            val argumentTypes: List<TypeExpr>, val returnType: TypeExpr
    ) : TypeExpr() {

        /**
         * @see [TypeExpr.substituteGenerics]
         */
        override fun substituteGenerics(map: Map<String, TypeExpr>): Function =
                Function(
                        argumentTypes = argumentTypes.map { it.substituteGenerics(map = map) },
                        returnType = returnType.substituteGenerics(map = map)
                )

        /**
         * @see [TypeExpr.checkTypeValidity]
         */
        override fun checkTypeValidity(environment: TypeCheckingEnv) {
            argumentTypes.forEach { it.checkTypeValidity(environment = environment) }
            returnType.checkTypeValidity(environment = environment)
        }

        /**
         * @see [TypeExpr.containsIdentifier]
         */
        override fun containsIdentifier(identifier: String): Boolean {
            return returnType.containsIdentifier(identifier = identifier)
                    || argumentTypes.any { it.containsIdentifier(identifier = identifier) }
        }

        /**
         * @see [TypeExpr.toPrefixed]
         */
        override fun toPrefixed(typeToPrefix: String, prefix: String): TypeExpr =
                TypeExpr.Function(
                        argumentTypes = argumentTypes.map { it.toPrefixed(typeToPrefix, prefix) },
                        returnType = returnType.toPrefixed(typeToPrefix, prefix)
                )

        /**
         * [toString] returns the string representation of the type.
         */
        override fun toString(): String =
                "(${argumentTypes.joinToString(separator = ", ")}) -> $returnType"

        /**
         * [equals] checks whether this type is equal to [other] object.
         */
        override fun equals(other: Any?): Boolean {
            if (this === other) {
                return true
            }
            if (other is TypeExpr.Function) {
                return argumentTypes == other.argumentTypes && returnType == other.returnType
            }
            return false
        }

        /**
         * [hashCode] returns the hashcode the type.
         */
        override fun hashCode(): Int = argumentTypes.hashCode() * 31 + returnType.hashCode()

    }

}
