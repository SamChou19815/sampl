package com.developersam.pl.sapl.ast.raw

import com.developersam.pl.sapl.TOP_LEVEL_MODULE_NAME
import com.developersam.pl.sapl.ast.decorated.DecoratedModule
import com.developersam.pl.sapl.ast.decorated.DecoratedModuleConstantMember
import com.developersam.pl.sapl.ast.decorated.DecoratedModuleMembers
import com.developersam.pl.sapl.ast.decorated.DecoratedProgram
import com.developersam.pl.sapl.ast.type.TypeInfo
import com.developersam.pl.sapl.environment.TypeCheckingEnv
import com.developersam.pl.sapl.exceptions.CompileTimeError
import com.developersam.pl.sapl.exceptions.IdentifierError

/**
 * [Module] node has a [name] and a set of ordered [members].
 */
data class Module(override val name: String, val members: ModuleMembers) : ModuleMember {

    override val isPublic: Boolean = true

    /**
     * [noNameShadowingValidation] validates that the members collection has no name shadowing by
     * checking whether there is a name conflict with a name in [set].
     *
     * @return [Unit]
     * @throws IdentifierError.ShadowedName if there is a detected shadowed name.
     */
    private fun noNameShadowingValidation(set: HashSet<String>) {
        if (!set.add(name)) {
            throw IdentifierError.ShadowedName(shadowedName = name)
        }
        val moduleNameValidator: (ModuleMember) -> Unit = { member ->
            val name = member.name
            if (!set.add(name)) {
                if (name == TOP_LEVEL_MODULE_NAME) {
                    throw IdentifierError.ForbiddenName(name = name)
                } else {
                    throw IdentifierError.ShadowedName(shadowedName = name)
                }
            }
        }
        members.typeMembers.forEach(moduleNameValidator)
        members.nestedModuleMembers.forEach { it.noNameShadowingValidation(set = set) }
        val memberSet = hashSetOf<String>()
        val memberNameValidator: (ModuleMember) -> Unit = { member ->
            val name = member.name
            if (!memberSet.add(name)) {
                throw IdentifierError.ShadowedName(shadowedName = name)
            }
        }
        members.constantMembers.forEach(memberNameValidator)
        members.functionMembers.forEach(memberNameValidator)
    }

    /**
     * [typeCheck] tries to type check this module under the given [TypeCheckingEnv] [e].
     * It returns a decorated module and a new environment after type check.
     */
    private fun typeCheck(e: TypeCheckingEnv): Pair<DecoratedModule, TypeCheckingEnv> {
        // Part 0: Members Declaration (for easier access only)
        val typeMembers = members.typeMembers
        val constantMembers = members.constantMembers
        val functionMembers = members.functionMembers
        val nestedModuleMembers = members.nestedModuleMembers
        // Part 1: Process Type Declarations
        val eInit = e.enterModule(module = this)
        typeMembers.forEach { it.typeCheck(environment = eInit) }
        // Part 2: Process Constant Definitions
        val decoratedConstants = arrayListOf<DecoratedModuleConstantMember>()
        val eWithConstants = constantMembers.fold(initial = eInit) { env, m ->
            val decoratedExpr = m.expr.typeCheck(environment = env)
            val decoratedConstant = DecoratedModuleConstantMember(
                    isPublic = m.isPublic, identifier = m.identifier, expr = decoratedExpr,
                    type = decoratedExpr.type
            )
            decoratedConstants.add(element = decoratedConstant)
            env.put(variable = m.identifier, typeInfo = decoratedExpr.type.asTypeInformation)
        }
        // Part 3: Process Function Definitions
        val eWithFunctions = eWithConstants.update(
                newTypeEnv = functionMembers.fold(initial = eWithConstants.typeEnv) { env, m ->
                    val functionTypeInfo =
                            TypeInfo(m.functionType, m.genericsDeclaration)
                    env.put(key = m.identifier, value = functionTypeInfo)
                })
        val decoratedFunctions = functionMembers.map { it.typeCheck(environment = eWithFunctions) }
        // Part 4: Process Nested Modules
        val decoratedModules = arrayListOf<DecoratedModule>()
        val eWithModules = nestedModuleMembers.fold(initial = eWithFunctions) { env, m ->
            val (decoratedModule, newEnv) = m.typeCheck(env)
            decoratedModules.add(element = decoratedModule)
            newEnv
        }
        // Part 5: Exit Current Module and Return
        val decoratedModule = DecoratedModule(name = name, members = DecoratedModuleMembers(
                typeMembers = typeMembers, constantMembers = decoratedConstants,
                functionMembers = decoratedFunctions, nestedModuleMembers = decoratedModules
        ))
        val eFinal = eWithModules.exitModule(module = this)
        return decoratedModule to eFinal
    }

    /**
     * [typeCheck] tries to type check this top-level module.
     * If it does not type check, it will throw an [CompileTimeError]
     *
     * @return the decorated program after type check.
     */
    fun typeCheck(): DecoratedProgram {
        noNameShadowingValidation(set = hashSetOf())
        val members = typeCheck(e = TypeCheckingEnv.initial).first.members
        return DecoratedProgram(members = members)
    }

}
