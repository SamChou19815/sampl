package com.developersam.pl.sapl.typecheck

import com.developersam.pl.sapl.ast.AstVisitor
import com.developersam.pl.sapl.ast.Expression
import com.developersam.pl.sapl.ast.Module
import com.developersam.pl.sapl.ast.PredefinedTypes
import com.developersam.pl.sapl.ast.TypeIdentifier

/**
 * [TypeCheckerVisitor] visits the AST while doing type checking.
 *
 * @param environment the environment that keeps track of the current module and type bindings.
 */
internal class TypeCheckerVisitor(
        private val environment: TypeCheckerEnvironment
) : AstVisitor<TypeIdentifier> {

    override fun visit(module: Module): TypeIdentifier {
        module.members.noNameShadowingValidation()
        // TODO
        return PredefinedTypes.moduleTypeIdentifier
    }

    override fun visit(expression: Expression): TypeIdentifier =
            expression.inferType(environment = environment)

}
