package com.developersam.pl.sapl.ast.raw

import com.developersam.pl.sapl.TOP_LEVEL_MODULE_NAME
import com.developersam.pl.sapl.ast.decorated.DecoratedClass
import com.developersam.pl.sapl.ast.decorated.DecoratedClassConstantMember
import com.developersam.pl.sapl.ast.decorated.DecoratedClassMembers
import com.developersam.pl.sapl.ast.decorated.DecoratedProgram
import com.developersam.pl.sapl.ast.type.TypeDeclaration
import com.developersam.pl.sapl.ast.type.TypeIdentifier
import com.developersam.pl.sapl.ast.type.TypeInfo
import com.developersam.pl.sapl.environment.TypeCheckingEnv
import com.developersam.pl.sapl.exceptions.CompileTimeError
import com.developersam.pl.sapl.exceptions.IdentifierError

/**
 * [Clazz] node has an type identifier with generics [identifier], a type [declaration] and a set
 * of ordered [members].
 * It means class.
 */
data class Clazz(
        val identifier: TypeIdentifier,
        val declaration: TypeDeclaration,
        val members: ClassMembers
) : ClassMember {

    override val name: String get() = identifier.name

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
            if (name == TOP_LEVEL_MODULE_NAME) {
                throw IdentifierError.ForbiddenName(name = name)
            } else {
                throw IdentifierError.ShadowedName(shadowedName = name)
            }
        }
        // TODO use functional DFS instead
        members.nestedClassMembers.forEach { it.noNameShadowingValidation(set = set) }
        val memberSet = hashSetOf<String>()
        val memberNameValidator: (ClassMember) -> Unit = { member ->
            val name = member.name
            if (!memberSet.add(name)) {
                throw IdentifierError.ShadowedName(shadowedName = name)
            }
        }
        members.constantMembers.forEach(memberNameValidator)
        members.functionMembers.forEach(memberNameValidator)
    }

    /**
     * [typeCheckTypeDeclaration] uses the given [environment] to type check the type declaration.
     *
     * Requires: [e] must already put all the type members inside to allow
     * recursive types.
     */
    private fun typeCheckTypeDeclaration(e: TypeCheckingEnv) {
        val newDeclaredTypes = identifier.genericsInfo
                .fold(initial = e.declaredTypes) { acc, s ->
                    acc.put(key = s, value = emptyList())
                }
        val newEnv = e.copy(declaredTypes = newDeclaredTypes)
        when (declaration) {
            is TypeDeclaration.Variant -> declaration.map.values
                    .forEach { it?.checkTypeValidity(environment = newEnv) }
            is TypeDeclaration.Struct -> declaration.map.values
                    .forEach { it.checkTypeValidity(environment = newEnv) }
        }
    }

    /**
     * [typeCheck] tries to type check this module under the given [TypeCheckingEnv] [e].
     * It returns a decorated module and a new environment after type check.
     */
    private fun typeCheck(e: TypeCheckingEnv): Pair<DecoratedClass, TypeCheckingEnv> {
        // Part 0: Members Declaration (for easier access only)
        val constantMembers = members.constantMembers
        val functionMembers = members.functionMembers
        val nestedModuleMembers = members.nestedClassMembers
        // Part 1: Process Type Declarations
        val eInit = e.enterModule(module = this)
        typeCheckTypeDeclaration(e = eInit)
        // Part 2: Process Constant Definitions
        val decoratedConstants = arrayListOf<DecoratedClassConstantMember>()
        val eWithConstants = constantMembers.fold(initial = eInit) { env, m ->
            val decoratedExpr = m.expr.typeCheck(environment = env)
            val decoratedConstant = DecoratedClassConstantMember(
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
        // Part 4: Process Nested Classes
        val decoratedModules = arrayListOf<DecoratedClass>()
        val eWithModules = nestedModuleMembers.fold(initial = eWithFunctions) { env, m ->
            val (decoratedModule, newEnv) = m.typeCheck(env)
            decoratedModules.add(element = decoratedModule)
            newEnv
        }
        // Part 5: Exit Current Module and Return
        val decoratedModule = DecoratedClass(
                identifier = identifier,
                declaration = declaration,
                members = DecoratedClassMembers(
                        constantMembers = decoratedConstants,
                        functionMembers = decoratedFunctions,
                        nestedClassMembers = decoratedModules
                )
        )
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
        val initialSet = hashSetOf<String>()
        initialSet.add(element = TOP_LEVEL_MODULE_NAME)
        noNameShadowingValidation(set = initialSet)
        val module = typeCheck(e = TypeCheckingEnv.initial).first
        return DecoratedProgram(module = module)
    }

}
