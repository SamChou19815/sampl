package com.developersam.pl.sapl.ast.raw

import com.developersam.pl.sapl.ast.protocol.Printable
import com.developersam.pl.sapl.ast.type.TypeDeclaration
import com.developersam.pl.sapl.ast.type.TypeIdentifier
import com.developersam.pl.sapl.config.IndentationStrategy
import com.developersam.pl.sapl.environment.TypeCheckingEnv

/**
 * [ModuleTypeMember] represents a type declaration of the form:
 * `public/private`([isPublic]) `type` [identifier] `=` [declaration].
 */
data class ModuleTypeMember(
        override val isPublic: Boolean,
        val identifier: TypeIdentifier, val declaration: TypeDeclaration
) : ModuleMember, Printable {

    override val name: String = identifier.name

    /**
     * [typeCheck] uses the given [environment] to type check this function member.
     *
     * Requires: [environment] must already put all the function members inside to allow mutually
     * recursive types.
     */
    fun typeCheck(environment: TypeCheckingEnv) {
        val newDeclaredTypes = identifier.genericsInfo
                .fold(initial = environment.declaredTypes) { acc, s ->
                    acc.put(key = s, value = emptyList())
                }
        val newEnv = environment.copy(declaredTypes = newDeclaredTypes)
        when (declaration) {
            is TypeDeclaration.Variant -> declaration.map.values
                    .forEach { it?.checkTypeValidity(environment = newEnv) }
            is TypeDeclaration.Struct -> declaration.map.values
                    .forEach { it.checkTypeValidity(environment = newEnv) }
        }

    }

    override fun prettyPrint(level: Int, builder: StringBuilder) {
        IndentationStrategy.indent2(level, builder)
        if (!isPublic) {
            builder.append("private ")
        }
        builder.append("type ")
        identifier.prettyPrint(builder = builder)
        when (declaration) {
            is TypeDeclaration.Variant -> {
                builder.append(" =\n")
                declaration.prettyPrint(level = level + 1, builder = builder)
            }
            is TypeDeclaration.Struct -> {
                builder.append(" = {\n")
                declaration.prettyPrint(level = level + 1, builder = builder)
                IndentationStrategy.indent2(level, builder).append("}\n")
            }
        }
    }

}
