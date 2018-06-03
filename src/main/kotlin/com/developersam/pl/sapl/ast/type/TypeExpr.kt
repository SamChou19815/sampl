package com.developersam.pl.sapl.ast.type

import com.developersam.pl.sapl.ast.protocol.Transpilable
import com.developersam.pl.sapl.codegen.TranspilerVisitor
import com.developersam.pl.sapl.codegen.IndentationQueue
import com.developersam.pl.sapl.environment.TypeCheckingEnv
import com.developersam.pl.sapl.exceptions.IdentifierError
import com.developersam.pl.sapl.util.joinToGenericsInfoString
import kotlin.math.min

/**
 * [TypeExpr] represents a set of supported type expression in type annotation.
 */
sealed class TypeExpr : Transpilable, Comparable<TypeExpr> {

    /**
     * [asTypeInformation] converts itself to [TypeInfo] without generics declaration.
     */
    val asTypeInformation: TypeInfo
        get() = TypeInfo(typeExpr = this)

    override fun acceptTranspilation(q: IndentationQueue, visitor: TranspilerVisitor): Unit =
            visitor.visit(q = q, typeExpr = this)

    override fun compareTo(other: TypeExpr): Int {
        if (this is Identifier && other is Identifier) {
            val c = type.compareTo(other = other.type)
            if (c != 0) {
                return c
            }
            val l = min(genericsInfo.size, other.genericsInfo.size)
            for (i in 0 until l) {
                val cc = genericsInfo[i].compareTo(other = other.genericsInfo[i])
                if (cc != 0) {
                    return cc
                }
            }
            return 0
        } else if (this is Function && other is Function) {
            val c = argumentType.compareTo(other = other.argumentType)
            if (c != 0) {
                return c
            }
            return returnType.compareTo(other = other.returnType)
        } else if (this is Identifier && other is Function) {
            return -1
        } else {
            return 1
        }
    }

    /**
     * [substituteGenerics] uses the given [map] to substitute generic symbols in the type
     * expression with concrete value types.
     */
    abstract fun substituteGenerics(map: Map<String, TypeExpr>): TypeExpr

    /**
     * [checkTypeValidity] tries to check the type is well-formed under the current given
     * [environment]. If not, it should throw [UndefinedTypeIdentifierError]
     */
    abstract fun checkTypeValidity(environment: TypeCheckingEnv)

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

        override fun toString(): String =
                if (genericsInfo.isEmpty()) type else {
                    type + genericsInfo.joinToGenericsInfoString()
                }

    }

    /**
     * [Function] represents the function type in the type annotation of the form
     * [argumentType] `->` [returnType].
     */
    data class Function(
            val argumentType: TypeExpr, val returnType: TypeExpr
    ) : TypeExpr() {

        override fun substituteGenerics(map: Map<String, TypeExpr>): Function =
                Function(
                        argumentType = argumentType.substituteGenerics(map = map),
                        returnType = returnType.substituteGenerics(map = map)
                )

        override fun checkTypeValidity(environment: TypeCheckingEnv) {
            argumentType.checkTypeValidity(environment = environment)
            returnType.checkTypeValidity(environment = environment)
        }

        override fun toString(): String = "($argumentType -> $returnType)"

    }

}
