package com.developersam.pl.sapl.typecheck

import com.developersam.fp.FpMap
import com.developersam.pl.sapl.ast.Module
import com.developersam.pl.sapl.ast.TypeInformation
import com.developersam.pl.sapl.exceptions.CompileTimeError
import com.developersam.pl.sapl.exceptions.UnexpectedTypeError

/**
 * [TypeChecker] defines a type checker that type checks modules.
 */
internal object TypeChecker {

    /**
     * [typeCheck] tries to type check the given top-level [module].
     *
     * If it does not type check, it will throw an [CompileTimeError]
     */
    fun typeCheck(module: Module) {
        typeCheckModule(module = module, e = TypeCheckerEnv.empty)
    }

    /**
     * [typeCheckModule] tries to type check a [module] under the given [TypeCheckerEnv] [e] and
     * returns a new environment after type check.
     */
    private fun typeCheckModule(e: TypeCheckerEnv, module: Module): TypeCheckerEnv {
        // conflict checker
        module.noNameShadowingValidation()
        // members
        val members = module.members
        val typeMembers = members.typeMembers
        val constantMembers = members.constantMembers
        val functionMembers = members.functionMembers
        val nestedModuleMembers = members.nestedModuleMembers
        // processed type declarations
        val init = TypeCheckerEnv(
                typeDefinitions = typeMembers.fold(e.typeDefinitions) { acc, m ->
                    acc.put(key = m.identifier, value = m.declaration)
                },
                upperLevelTypeEnv = e.upperLevelTypeEnv
        )
        // process constant definitions
        val eWithC = constantMembers.fold(init) { env, m ->
            env.put(variable = m.identifier, typeInfo = m.expr.inferType(env).asTypeInformation)
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
            val bodyType = m.body.inferType(environment = eWithF)
            if (expectedType != bodyType) {
                throw UnexpectedTypeError(expectedType = expectedType, actualType = bodyType)
            }
        }
        // process nested modules
        val envWithModules = nestedModuleMembers.fold(eWithF, ::typeCheckModule)
        // remove private members
        var envTemp = constantMembers.fold(envWithModules) { env, m ->
            if (m.isPublic) env else env.remove(variable = m.identifier)
        }
        envTemp = functionMembers.fold(envTemp) { env, m ->
            if (m.isPublic) env else env.remove(variable = m.identifier)
        }
        // move current level to upper level
        val newUpperLevel = envTemp.currentLevelTypeEnv
                .mapByKey { k -> "${module.name}.$k" }
                .reduce(envTemp.upperLevelTypeEnv) { k, v, acc -> acc.put(key = k, value = v) }
        return envTemp.copy(upperLevelTypeEnv = newUpperLevel, currentLevelTypeEnv = FpMap.empty())
    }

}
