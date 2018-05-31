package com.developersam.pl.sapl.ast.raw

import com.developersam.fp.FpMap
import com.developersam.pl.sapl.TOP_LEVEL_MODULE_NAME
import com.developersam.pl.sapl.ast.TypeInformation
import com.developersam.pl.sapl.exceptions.CompileTimeError
import com.developersam.pl.sapl.exceptions.ForbiddenNameError
import com.developersam.pl.sapl.exceptions.ShadowedNameError
import com.developersam.pl.sapl.exceptions.UnexpectedTypeError
import com.developersam.pl.sapl.typecheck.TypeCheckerEnvironment

/**
 * [Module] node has a [name] and a set of ordered [members].
 */
internal data class Module(
        override val name: String,
        val members: ModuleMembers
) : ModuleMember {

    /**
     * [noNameShadowingValidation] validates that the members collection has no name shadowing by
     * checking whether there is a name conflict with a name in [set].
     *
     * @return [Unit]
     * @throws ShadowedNameError if there is a detected shadowed name.
     */
    private fun noNameShadowingValidation(set: HashSet<String>) {
        if (!set.add(name)) {
            throw ShadowedNameError(shadowedName = name)
        }
        val moduleNameValidator: (ModuleMember) -> Unit = { member ->
            val name = member.name
            if (!set.add(name)) {
                if (name == TOP_LEVEL_MODULE_NAME) {
                    throw ForbiddenNameError(name = name)
                } else {
                    throw ShadowedNameError(shadowedName = name)
                }
            }
        }
        members.typeMembers.forEach(moduleNameValidator)
        members.nestedModuleMembers.forEach { it.noNameShadowingValidation(set = set) }
        val memberSet = hashSetOf<String>()
        val memberNameValidator: (ModuleMember) -> Unit = { member ->
            val name = member.name
            if (!memberSet.add(name)) {
                throw ShadowedNameError(shadowedName = name)
            }
        }
        members.constantMembers.forEach(memberNameValidator)
        members.functionMembers.forEach(memberNameValidator)
    }

    /**
     * [typeCheck] tries to type check this module under the given [TypeCheckerEnvironment] [e] and
     * returns a new environment after type check.
     */
    private fun typeCheck(e: TypeCheckerEnvironment): TypeCheckerEnvironment {// conflict checker
        noNameShadowingValidation(set = hashSetOf())
        // members
        val typeMembers = members.typeMembers
        val constantMembers = members.constantMembers
        val functionMembers = members.functionMembers
        val nestedModuleMembers = members.nestedModuleMembers
        // processed type declarations
        val init = TypeCheckerEnvironment(
                typeDefinitions = typeMembers.fold(e.typeDefinitions) { acc, m ->
                    acc.put(key = m.identifier, value = m.declaration)
                },
                upperLevelTypeEnv = e.upperLevelTypeEnv
        )
        // process constant definitions
        val eWithC = constantMembers.fold(init) { env, m ->
            val decoratedExpr = m.expr.typeCheck(environment = env)
            env.put(variable = m.identifier, typeInfo = decoratedExpr.type.asTypeInformation)
        }
        // process function definitions
        val eWithF = eWithC.update(
                newCurrent = functionMembers.fold(eWithC.currentLevelTypeEnv) { env, m ->
                    env.put(key = m.identifier, value = TypeInformation(
                            typeExpr = m.functionType, genericInfo = m.genericsDeclaration
                    ))
                })
        functionMembers.forEach { m ->
            val expectedType = m.functionType.returnType
            val bodyType = m.body.typeCheck(environment = eWithF).type
            if (expectedType != bodyType) {
                throw UnexpectedTypeError(expectedType = expectedType, actualType = bodyType)
            }
        }
        // process nested modules
        val envWithModules = nestedModuleMembers.fold(eWithF) { env, m -> m.typeCheck(env) }
        // remove private members
        var envTemp = constantMembers.fold(envWithModules) { env, m ->
            if (m.isPublic) env else env.remove(variable = m.identifier)
        }
        envTemp = functionMembers.fold(envTemp) { env, m ->
            if (m.isPublic) env else env.remove(variable = m.identifier)
        }
        // move current level to upper level
        val newUpperLevel = envTemp.currentLevelTypeEnv
                .mapByKey { k -> "$name.$k" }
                .reduce(envTemp.upperLevelTypeEnv) { k, v, acc -> acc.put(key = k, value = v) }
        return envTemp.copy(upperLevelTypeEnv = newUpperLevel, currentLevelTypeEnv = FpMap.empty())
    }

    /**
     * [typeCheck] tries to type check this top-level module.
     *
     * If it does not type check, it will throw an [CompileTimeError]
     */
    fun typeCheck() {
        typeCheck(e = TypeCheckerEnvironment.empty)
    }

}
