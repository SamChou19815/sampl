package com.developersam.pl.sapl.ast.protocol

import com.developersam.pl.sapl.config.IndentationStrategy
import com.developersam.pl.sapl.config.IndentationStrategy.TWO_SPACES

/**
 * [Printable] specifies how an AST node can be printed.
 */
interface Printable {

    /**
     * [prettyPrint] prints the source code of the AST node in well formatted way.
     * It does not need to worry about the x-character-limit issue, but it does need to consider
     * proper indentation.
     *
     * @param level the current indentation level, which is indentation string independent.
     * The starting level is 0, which means a top level declaration or a simple local expression
     * (in this case the implementation should choose to ignore this parameter).
     * @param builder the [StringBuilder] to add source code in.
     */
    fun prettyPrint(level: Int = 0, builder: StringBuilder)

    /**
     * [prettyPrint] prints the source code of the AST node in well formatted way.
     * It does not need to worry about the x-character-limit issue, but it does need to consider
     * proper indentation.
     * Calling this function assumes that you are at indentation level 0.
     */
    fun prettyPrint(): String =
            StringBuilder().apply { prettyPrint(level = 0, builder = this) }.toString()

}
