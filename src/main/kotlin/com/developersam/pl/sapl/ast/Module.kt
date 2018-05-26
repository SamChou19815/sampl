package com.developersam.pl.sapl.ast

/**
 * [Module] node has a [name] and a set of ordered [members].
 */
data class Module(val name: String, val members: List<ModuleMember>)

/**
 * [ModuleMember] represents a set of supported module members.
 */
sealed class ModuleMember

/**
 * [NestedModule] represents a nested [module], which is considered a member of a module.
 */
data class NestedModule(val module: Module) : ModuleMember()

/**
 * [ModuleTypeMember] represents a type declaration of the form:
 * `public/private`([isPublic]) `type` [identifier] `=` [declaration].
 */
class ModuleTypeMember(
        val isPublic: Boolean, val identifier: TypeIdentifier,
        val declaration: TypeExprInDeclaration
) : ModuleMember()

/**
 * [ModuleConstantMember] represents a constant declaration of the form:
 * `public/private`([isPublic]) `let` [identifier] `=` [expr].
 */
class ModuleConstantMember(
        val isPublic: Boolean, val identifier: String, val expr: Expression
) : ModuleMember()

/**
 * [ModuleFunctionMember] represents a function declaration of the form:
 * `public/private`([isPublic]) `let` [identifier] ([genericsDeclaration])?
 * [arguments] `:` [returnType] `=` [body].
 */
class ModuleFunctionMember(
        val isPublic: Boolean, val identifier: String,
        val genericsDeclaration: Set<String>,
        val arguments: List<Pair<String, TypeExprInAnnotation>>,
        val returnType: TypeExprInAnnotation, val body: Expression
) : ModuleMember()
