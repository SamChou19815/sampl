package com.developersam.pl.sapl

import com.developersam.pl.sapl.antlr.LanguageBaseVisitor
import com.developersam.pl.sapl.antlr.LanguageParser.*

/**
 * [CompilerTypeCheckingEnvironment] creates an environment for the compiler to do the type checking
 * under the current context.
 */
class CompilerTypeCheckingEnvironment : LanguageBaseVisitor<Unit>() {

    override fun visitCompilationUnit(ctx: CompilationUnitContext) {

    }

    override fun visitClassTypeDeclaration(ctx: ClassTypeDeclarationContext) {

    }

    override fun visitClassConstantDeclaration(ctx: ClassConstantDeclarationContext) {

    }

    override fun visitClassMethodDeclaration(ctx: ClassMethodDeclarationContext) {

    }

}