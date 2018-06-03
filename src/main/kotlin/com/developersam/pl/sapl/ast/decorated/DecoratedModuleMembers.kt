package com.developersam.pl.sapl.ast.decorated

import com.developersam.pl.sapl.ast.protocol.Printable
import com.developersam.pl.sapl.ast.raw.ModuleTypeMember
import com.developersam.pl.sapl.codegen.IndentationQueue

/**
 * [DecoratedModuleMembers] contains collections of different types of module members,
 * in order of declaration.
 */
data class DecoratedModuleMembers(
        val typeMembers: List<ModuleTypeMember>,
        val constantMembers: List<DecoratedModuleConstantMember>,
        val functionMembers: List<DecoratedModuleFunctionMember>,
        val nestedModuleMembers: List<DecoratedModule>
) : Printable {

    override fun prettyPrint(q: IndentationQueue) {
        val printerAction: (Printable) -> Unit = { m ->
            m.prettyPrint(q = q)
            q.addEmptyLine()
        }
        typeMembers.forEach(action = printerAction)
        constantMembers.forEach(action = printerAction)
        functionMembers.forEach(action = printerAction)
        nestedModuleMembers.forEach(action = printerAction)
    }

}
