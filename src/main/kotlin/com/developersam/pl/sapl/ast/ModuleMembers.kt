package com.developersam.pl.sapl.ast

import com.developersam.pl.sapl.exceptions.ShadowedNameError
import com.developersam.pl.sapl.util.toFunctionTypeExpr

/**
 * [ModuleMembers] contains collections of different types of module members,
 * in order of declaration.
 */
internal data class ModuleMembers(
        val typeMembers: List<ModuleTypeMember>,
        val constantMembers: List<ModuleConstantMember>,
        val functionMembers: List<ModuleFunctionMember>,
        val nestedModuleMembers: List<Module>
)

/**
 * [ModuleTypeMember] represents a type declaration of the form:
 * `type` [identifier] `=` [declaration].
 */
internal data class ModuleTypeMember(
        val identifier: TypeIdentifier,
        val declaration: TypeExprInDeclaration
) : ModuleMember {
    override val name: String = identifier.type
}

/**
 * [ModuleConstantMember] represents a constant declaration of the form:
 * `public/private`([isPublic]) `let` [identifier] `=` [expr].
 */
internal data class ModuleConstantMember(
        val isPublic: Boolean, val identifier: String, val expr: Expression
) : ModuleMember {
    override val name: String = identifier
}

/**
 * [ModuleFunctionMember] represents a function declaration of the form:
 * `public/private`([isPublic]) `let` [identifier] ([genericsDeclaration])?
 * [arguments] `:` [returnType] `=` [body].
 */
internal data class ModuleFunctionMember(
        val isPublic: Boolean, val identifier: String,
        val genericsDeclaration: List<String>,
        val arguments: List<Pair<String, TypeExprInAnnotation>>,
        val returnType: TypeExprInAnnotation, val body: Expression
) : ModuleMember {

    override val name: String = identifier

    /**
     * [functionType] reports the functional type of itself.
     */
    val functionType: FunctionTypeInAnnotation = toFunctionTypeExpr(
            argumentTypes = arguments.map { it.second },
            returnType = returnType
    )

}
