package com.developersam.pl.sapl.ast.raw

import com.developersam.pl.sapl.ast.protocol.Printable
import com.developersam.pl.sapl.ast.type.TypeDeclaration
import com.developersam.pl.sapl.ast.type.TypeIdentifier
import com.developersam.pl.sapl.codegen.IndentationQueue
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

    override fun prettyPrint(q: IndentationQueue) {
        val firstLineCommon = StringBuilder().apply {
            if (!isPublic) {
                append("private ")
            }
            append("type ").append(identifier).append(" =")
        }.toString()
        when (declaration) {
            is TypeDeclaration.Variant -> {
                q.addLine(line = firstLineCommon)
                q.indentAndApply { declaration.prettyPrint(q = this) }
            }
            is TypeDeclaration.Struct -> {
                q.addLine(line = "$firstLineCommon {")
                q.indentAndApply { declaration.prettyPrint(q = this) }
                q.addLine(line = "}")
            }
        }
    }

    override fun toString(): String = asIndentedSourceCode

}
