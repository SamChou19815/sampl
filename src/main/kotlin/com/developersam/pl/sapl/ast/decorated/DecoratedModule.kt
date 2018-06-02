package com.developersam.pl.sapl.ast.decorated

/**
 * [DecoratedModule] node has a [name] and a set of ordered [members].
 * It contains decorated ASTs.
 */
data class DecoratedModule(val name: String, val members: DecoratedModuleMembers) {

    override fun toString(): String {
        return """
module $name {

$members
}
        """.trimIndent()
    }

}
