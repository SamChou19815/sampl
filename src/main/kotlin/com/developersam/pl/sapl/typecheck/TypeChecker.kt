package com.developersam.pl.sapl.typecheck

import com.developersam.fp.FpMap
import com.developersam.pl.sapl.ast.Module
import com.developersam.pl.sapl.ast.ModuleConstantMember
import com.developersam.pl.sapl.ast.TypeExprInAnnotation
import com.developersam.pl.sapl.ast.TypeExprInDeclaration
import com.developersam.pl.sapl.ast.TypeIdentifier

/**
 * [Def] is the collection of definition for type checking.
 */
private typealias Def = FpMap<TypeIdentifier, TypeExprInDeclaration>

/**
 * [Env] is the environment for type checking
 */
private typealias Env = FpMap<String, TypeExprInAnnotation>

/**
 * [TypeChecker] defines how a type checker should work.
 *
 * @param module the module to type check.
 * @param parentTypeChecker the type checker at the parent module. It can be `null`, which means
 * that this type checker is at the top level.
 */
internal class TypeChecker(
        module: Module,
        parentTypeChecker: TypeChecker? = null
) {

    private var currentModuleTracker: CurrentModuleTracker =
            parentTypeChecker?.currentModuleTracker?.enterSubModule(subModuleName = module.name)
                    ?: CurrentModuleTracker(module.name)
    private var parentTypeDefinitions: Def = TODO()
    private val currentTypeDefinitions: Def
    private var parentTypeEnvironment: Env = TODO()
    private var currentTypeEnvironment: Env = FpMap.empty()

    init {
        val members = module.members
        // conflict checker
        members.noNameShadowingValidation()
        // process type definitions
        currentTypeDefinitions = members.typeMembers.fold(initial = FpMap.empty()) { acc, member ->
            acc.put(key = member.identifier, value = member.declaration)
        }
        // process constant definitions
        val typeEnvAfterTypeCheckingConstants: Env = members.constantMembers.fold(
                initial = FpMap.empty(), operation = ::typeCheckConstant)
    }

    private fun typeCheckConstant(env: Env, constantMember: ModuleConstantMember): Env {
        constantMember.expr.inferType(environment = this)
        TODO()
    }

}
