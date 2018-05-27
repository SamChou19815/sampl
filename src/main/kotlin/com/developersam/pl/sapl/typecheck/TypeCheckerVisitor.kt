package com.developersam.pl.sapl.typecheck

import com.developersam.pl.sapl.ast.AstVisitor
import com.developersam.pl.sapl.ast.BinaryExpr
import com.developersam.pl.sapl.ast.BoolLiteral
import com.developersam.pl.sapl.ast.CharLiteral
import com.developersam.pl.sapl.ast.Expression
import com.developersam.pl.sapl.ast.FloatLiteral
import com.developersam.pl.sapl.ast.FunctionApplicationExpr
import com.developersam.pl.sapl.ast.FunctionExpr
import com.developersam.pl.sapl.ast.IfElseExpr
import com.developersam.pl.sapl.ast.IntLiteral
import com.developersam.pl.sapl.ast.LetExpr
import com.developersam.pl.sapl.ast.Literal
import com.developersam.pl.sapl.ast.LiteralExpr
import com.developersam.pl.sapl.ast.MatchExpr
import com.developersam.pl.sapl.ast.MemberAccessExpr
import com.developersam.pl.sapl.ast.Module
import com.developersam.pl.sapl.ast.ModuleConstantMember
import com.developersam.pl.sapl.ast.ModuleFunctionMember
import com.developersam.pl.sapl.ast.ModuleMember
import com.developersam.pl.sapl.ast.ModuleTypeMember
import com.developersam.pl.sapl.ast.NestedModule
import com.developersam.pl.sapl.ast.NotExpr
import com.developersam.pl.sapl.ast.PredefinedTypes
import com.developersam.pl.sapl.ast.StringLiteral
import com.developersam.pl.sapl.ast.ThrowExpr
import com.developersam.pl.sapl.ast.TryCatchFinallyExpr
import com.developersam.pl.sapl.ast.TypeIdentifier
import com.developersam.pl.sapl.ast.UnitLiteral
import com.developersam.pl.sapl.ast.VariableIdentifierExpr
import com.developersam.pl.sapl.environment.FunctionalEnvironment
import com.developersam.pl.sapl.exceptions.ShadowedNameError
import com.developersam.pl.sapl.exceptions.UndefinedIdentifierError

/**
 * [TypeCheckerVisitor] visits the AST while doing type checking.
 *
 * @param environment the environment that keeps track of the current module and type bindings.
 */
class TypeCheckerVisitor(
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

    override fun visit(literal: Literal): TypeIdentifier = when (literal) {
        is UnitLiteral -> PredefinedTypes.unitTypeIdentifier
        is IntLiteral -> PredefinedTypes.intTypeIdentifier
        is FloatLiteral -> PredefinedTypes.floatTypeIdentifier
        is BoolLiteral -> PredefinedTypes.boolTypeIdentifier
        is CharLiteral -> PredefinedTypes.charTypeIdentifier
        is StringLiteral -> PredefinedTypes.stringTypeIdentifier
    }

    override fun visit(expression: Expression): TypeIdentifier = when (expression) {
        is LiteralExpr -> visit(literal = expression.literal)
        is VariableIdentifierExpr -> {
            val v = expression.variable
            environment.typesEnvironment[v] ?: throw UndefinedIdentifierError(badIdentifier = v)
        }
        is MemberAccessExpr -> {
            TODO()
        }
        is FunctionApplicationExpr -> {
            TODO()
        }
        is BinaryExpr -> {
            TODO()
        }
        is NotExpr -> {
            TODO()
        }
        is LetExpr -> {
            TODO()
        }
        is FunctionExpr -> {
            TODO()
        }
        is IfElseExpr -> {
            TODO()
        }
        is MatchExpr -> {
            TODO()
        }
        is ThrowExpr -> {
            TODO()
        }
        is TryCatchFinallyExpr -> {
            TODO()
        }
    }

}
