package org.sampl.ast.raw

import org.sampl.ast.decorated.DecoratedClass
import org.sampl.ast.decorated.DecoratedClassConstantMember
import org.sampl.ast.decorated.DecoratedClassMembers
import org.sampl.ast.decorated.DecoratedProgram
import org.sampl.ast.type.TypeDeclaration
import org.sampl.ast.type.TypeIdentifier
import org.sampl.ast.type.TypeInfo
import org.sampl.environment.TypeCheckingEnv
import org.sampl.exceptions.CompileTimeError
import org.sampl.exceptions.IdentifierError
import org.sampl.runtime.RuntimeLibrary
import org.sampl.runtime.withInjectedRuntime

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
            throw IdentifierError.ShadowedName(shadowedName = name)
        }
        members.nestedClassMembers.forEach { it.noNameShadowingValidation(set = set) }
        set.remove(name)
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
     * [typeCheckTypeDeclaration] uses the given [e] to type check the type declaration.
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
     * [typeCheck] tries to type check this class under the given [TypeCheckingEnv] [e].
     * It returns a decorated class and a new environment after type check.
     */
    private fun typeCheck(e: TypeCheckingEnv): Pair<DecoratedClass, TypeCheckingEnv> {
        // Part 0: Members Declaration (for easier access only)
        val constantMembers = members.constantMembers
        val functionMembers = members.functionMembers
        val nestedModuleMembers = members.nestedClassMembers
        // Part 1: Process Type Declarations
        val eInit = e.enterClass(clazz = this)
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
        val decoratedClasses = arrayListOf<DecoratedClass>()
        val eWithClasses = nestedModuleMembers.fold(initial = eWithFunctions) { env, m ->
            val (decoratedModule, newEnv) = m.typeCheck(env)
            decoratedClasses.add(element = decoratedModule)
            newEnv
        }
        // Part 5: Exit Current Module and Return
        val decoratedClass = DecoratedClass(
                identifier = identifier,
                declaration = declaration,
                members = DecoratedClassMembers(
                        constantMembers = decoratedConstants,
                        functionMembers = decoratedFunctions,
                        nestedClassMembers = decoratedClasses
                )
        )
        val eFinal = eWithClasses.exitClass(clazz = this)
        return decoratedClass to eFinal
    }

    /**
     * [typeCheck] tries to type check this top-level class with an optional
     * [providedRuntimeLibrary] as the type checking context.
     * If it does not type check, it will throw an [CompileTimeError]
     *
     * @return the decorated program after type check.
     */
    fun typeCheck(providedRuntimeLibrary: RuntimeLibrary? = null): DecoratedProgram {
        noNameShadowingValidation(set = hashSetOf())
        val clazz = withInjectedRuntime(providedRuntimeLibrary = providedRuntimeLibrary)
                .typeCheck(e = TypeCheckingEnv.initial)
                .first
        return DecoratedProgram(clazz = clazz, providedRuntimeLibrary = providedRuntimeLibrary)
    }

}
