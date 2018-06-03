package com.developersam.pl.sapl.ast.decorated

import com.developersam.pl.sapl.ast.protocol.Printable
import com.developersam.pl.sapl.config.IndentationStrategy

/**
 * [DecoratedModule] node has a [name] and a set of ordered [members].
 * It contains decorated ASTs.
 */
data class DecoratedModule(val name: String, val members: DecoratedModuleMembers) : Printable {

    override fun prettyPrint(level: Int, builder: StringBuilder) {
        IndentationStrategy.indent2(level, builder)
                .append("module ").append(name).append(" {\n\n")
        members.prettyPrint(level = level + 1, builder = builder)
        IndentationStrategy.indent2(level, builder).append("}\n")
    }

}
