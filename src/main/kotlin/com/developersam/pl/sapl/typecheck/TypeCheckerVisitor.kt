package com.developersam.pl.sapl.typecheck

import com.developersam.pl.sapl.ast.AstVisitor
import com.developersam.pl.sapl.ast.Expression
import com.developersam.pl.sapl.ast.Literal
import com.developersam.pl.sapl.ast.Module
import com.developersam.pl.sapl.ast.ModuleMember
import com.developersam.pl.sapl.ast.Pattern
import com.developersam.pl.sapl.ast.TypeExprInAnnotation
import com.developersam.pl.sapl.ast.TypeExprInDeclaration
import com.developersam.pl.sapl.ast.TypeIdentifier
import com.developersam.pl.sapl.exceptions.NameConflictException

/**
 * [TypeCheckerVisitor] visits the AST while doing type checking.
 */
object TypeCheckerVisitor : AstVisitor<TypeIdentifier> {

    override fun visit(module: Module): TypeIdentifier {
        val members = module.members
        // check namespace conflict
        val names: HashSet<String> = hashSetOf()
        for (member in members) {
            val name = member.name
            if (!names.add(name)) {
                throw NameConflictException(conflictedName = name)
            }
        }
        TODO("not implemented")
    }

    override fun visit(moduleMember: ModuleMember): TypeIdentifier {
        TODO("not implemented")
    }

    override fun visit(literal: Literal): TypeIdentifier {
        TODO("not implemented")
    }

    override fun visit(expression: Expression): TypeIdentifier {
        TODO("not implemented")
    }

    override fun visit(pattern: Pattern): TypeIdentifier {
        TODO("not implemented")
    }

    override fun visit(typeExprInAnnotation: TypeExprInAnnotation): TypeIdentifier {
        TODO("not implemented")
    }

    override fun visit(typeExprInDeclaration: TypeExprInDeclaration): TypeIdentifier {
        TODO("not implemented")
    }

}
