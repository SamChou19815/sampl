package com.developersam.pl.sapl.typecheck

import com.developersam.pl.sapl.ast.AstVisitor
import com.developersam.pl.sapl.ast.Expression
import com.developersam.pl.sapl.ast.Module
import com.developersam.pl.sapl.ast.ModuleConstantMember
import com.developersam.pl.sapl.ast.ModuleFunctionMember
import com.developersam.pl.sapl.ast.ModuleMember
import com.developersam.pl.sapl.ast.ModuleTypeMember
import com.developersam.pl.sapl.ast.NestedModule
import com.developersam.pl.sapl.ast.PredefinedTypes
import com.developersam.pl.sapl.ast.TypeIdentifier
import com.developersam.pl.sapl.exceptions.ShadowedNameError

/**
 * [TypeCheckerVisitor] visits the AST while doing type checking.
 *
 * @param environment the environment that keeps track of the current module and type bindings.
 */
internal class TypeCheckerVisitor(
        private val environment: TypeCheckerEnvironment
) : AstVisitor<TypeIdentifier> {

    override fun visit(module: Module): TypeIdentifier {
        val members = module.members
        // check namespace shadowing
        val names: HashSet<String> = hashSetOf()
        for (member in members) {
            val name = member.name
            if (!names.add(name)) {
                throw ShadowedNameError(shadowedName = name)
            }
        }
        // TODO
        return PredefinedTypes.moduleTypeIdentifier
    }

    override fun visit(moduleMember: ModuleMember): TypeIdentifier = when (moduleMember) {
        is NestedModule -> visit(module = moduleMember.module) // TODO enter module???
        is ModuleTypeMember -> {
            TODO()
        }
        is ModuleConstantMember -> {
            TODO()
        }
        is ModuleFunctionMember -> {
            TODO()
        }
    }

    override fun visit(expression: Expression): TypeIdentifier =
            expression.inferType(environment = environment)

}
