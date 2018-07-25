package org.sampl.ast.raw

import org.sampl.ast.decorated.DecoratedProgram
import org.sampl.ast.raw.ClassMember.Companion.typeCheck
import org.sampl.ast.type.TypeDeclaration
import org.sampl.ast.type.boolTypeName
import org.sampl.ast.type.charTypeName
import org.sampl.ast.type.floatTypeName
import org.sampl.ast.type.intTypeName
import org.sampl.ast.type.stringArrayTypeName
import org.sampl.ast.type.stringTypeName
import org.sampl.ast.type.unitTypeName
import org.sampl.environment.TypeCheckingEnv
import org.sampl.exceptions.CompileTimeError
import org.sampl.exceptions.IdentifierError
import org.sampl.runtime.RuntimeLibrary
import org.sampl.runtime.withInjectedRuntime

/**
 * [RawProgram] represents the top-level not-type-checked program with a list of [members].
 *
 * @property members a list of class members is a program.
 */
internal data class RawProgram(val members: List<ClassMember>) {

    /**
     * [noNameShadowingValidation] validates that the members collection has no name shadowing by
     * checking whether there is a name conflict with a name in [set], which stores used type and
     * class names.
     *
     * @return [Unit]
     * @throws IdentifierError.ShadowedName if there is a detected shadowed name.
     */
    private fun List<ClassMember>.noNameShadowingValidation(set: HashSet<String>) {
        val constantFunctionNameSet = hashSetOf<String>()
        for (member in this) {
            when (member) {
                is ClassMember.Constant -> {
                    val name = member.identifier
                    if (!constantFunctionNameSet.add(element = name)) {
                        throw IdentifierError.ShadowedName(
                                lineNo = member.identifierLineNo, shadowedName = name
                        )
                    }
                }
                is ClassMember.FunctionGroup -> {
                    for (f in member.functions) {
                        val name = f.identifier
                        if (!constantFunctionNameSet.add(element = name)) {
                            throw IdentifierError.ShadowedName(
                                    lineNo = f.identifierLineNo, shadowedName = name
                            )
                        }
                    }
                }
                is ClassMember.Clazz -> {
                    val name = member.identifier.name
                    if (!set.add(element = name)) {
                        throw IdentifierError.ShadowedName(
                                lineNo = member.identifierLineNo, shadowedName = name
                        )
                    }
                    if (member.declaration is TypeDeclaration.Variant) {
                        for (k in member.declaration.map.keys) {
                            if (!set.add(element = k)) {
                                throw IdentifierError.ShadowedName(
                                        lineNo = member.identifierLineNo, shadowedName = k
                                )
                            }
                        }
                    }
                    val classMembers = member.members
                    classMembers.noNameShadowingValidation(set = set)
                }
            }
        }
    }

    /**
     * [typeCheck] tries to type check this top-level program with an optional
     * [providedRuntimeLibrary] as the type checking context.
     * If it does not type check, it will throw an [CompileTimeError]
     *
     * @return the decorated program after type check.
     */
    fun typeCheck(providedRuntimeLibrary: RuntimeLibrary? = null): DecoratedProgram {
        members.noNameShadowingValidation(set = hashSetOf(
                unitTypeName, intTypeName, floatTypeName, boolTypeName,
                charTypeName, stringTypeName, stringArrayTypeName
        ))
        return members.withInjectedRuntime(providedRuntimeLibrary)
                .typeCheck(env = TypeCheckingEnv.initial)
                .first
                .let { DecoratedProgram(it, providedRuntimeLibrary) }
    }

}
