package com.developersam.pl.sapl.ast

/**
 * [Module] node has a [name] and a set of ordered [members].
 */
data class Module(val name: String, val members: List<ModuleMember>) : AstNode {

    override fun <T> accept(visitor: AstVisitor<T>): T = visitor.visit(module = this)

}

/**
 * [ModuleMember] represents a set of supported module members.
 */
sealed class ModuleMember : AstNode {

    /**
     * [name] is the name of the identifier of the module member.
     */
    abstract val name: String

    final override fun <T> accept(visitor: AstVisitor<T>): T = visitor.visit(moduleMember = this)

}

/**
 * [NestedModule] represents a nested [module], which is considered a member of a module.
 */
data class NestedModule(val module: Module) : ModuleMember() {
    override val name: String = module.name
}

/**
 * [ModuleTypeMember] represents a type declaration of the form:
 * `public/private`([isPublic]) `type` [identifier] `=` [declaration].
 */
class ModuleTypeMember(
        val isPublic: Boolean, val identifier: TypeIdentifier,
        val declaration: TypeExprInDeclaration
) : ModuleMember() {
    override val name: String = identifier.type
}

/**
 * [ModuleConstantMember] represents a constant declaration of the form:
 * `public/private`([isPublic]) `let` [identifier] `=` [expr].
 */
class ModuleConstantMember(
        val isPublic: Boolean, val identifier: String, val expr: Expression
) : ModuleMember() {
    override val name: String = identifier
}

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
) : ModuleMember() {
    override val name: String = identifier
}
