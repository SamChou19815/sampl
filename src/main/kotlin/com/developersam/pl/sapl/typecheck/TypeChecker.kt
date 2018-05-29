package com.developersam.pl.sapl.typecheck

import com.developersam.fp.FpMap
import com.developersam.pl.sapl.ast.Module
import com.developersam.pl.sapl.ast.ModuleConstantMember

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

    private val environment: TypeCheckerEnvironment

    init {
        val members = module.members
        // conflict checker
        members.noNameShadowingValidation()
        val environmentInit = TypeCheckerEnvironment(
                currentModuleTracker = parentTypeChecker
                        ?.environment?.currentModuleTracker
                        ?.enterSubModule(subModuleName = module.name)
                        ?: CurrentModuleTracker(topLevelModuleName = module.name),
                currentLevelTypeDefinitions = members.typeMembers
                        .fold(initial = FpMap.empty()) { acc, member ->
                            acc.put(key = member.identifier, value = member.declaration)
                        },
                upperLevelTypeEnvironment = parentTypeChecker?.environment
                        ?.upperLevelTypeEnvironment
                        ?: FpMap.empty(),
                currentLevelTypeEnvironment = FpMap.empty()
        )
        // process constant definitions
        val typeEnvAfterTypeCheckingConstants = members.constantMembers
                .fold(initial = environmentInit, operation = ::typeCheckConstant)
        // TODO
        environment = typeEnvAfterTypeCheckingConstants
    }

    private fun typeCheckConstant(env: TypeCheckerEnvironment,
                                  constantMember: ModuleConstantMember): TypeCheckerEnvironment =
            env.updateTypeInformation(
                    variable = constantMember.identifier,
                    typeInfo = constantMember.expr.inferType(environment = env)
            )

}
