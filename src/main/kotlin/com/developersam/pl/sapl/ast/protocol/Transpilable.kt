package com.developersam.pl.sapl.ast.protocol

import com.developersam.pl.sapl.codegen.IndentationQueue

/**
 * [Transpilable] specifies the methods that an AST node must support in order for the flexible
 * transpiler visitor to gradually transpile the code to readable format in another language.
 */
interface Transpilable {

    /**
     * [toIndentedTranspiledCode] returns the transpiled code of the AST node in well formatted way.
     * It does not need to worry about the x-character-limit issue, but it does need to consider
     * proper indentation.
     * Calling this function assumes that you are at indentation level 0.
     *
     * @param visitor the visitor of the transpiler that actually does the transpiling logic.
     */
    fun toIndentedTranspiledCode(visitor: TranspilerVisitor): String =
            IndentationQueue(strategy = visitor.indentationStrategy)
                    .apply { acceptTranspilation(q = this, visitor = visitor) }
                    .toIndentedCode()

    /**
     * [asInlineTranspiledCode] returns the source code of the AST node in one-liner way.
     *
     * @param visitor the visitor of the transpiler that actually does the transpiling logic.
     */
    fun asInlineTranspiledCode(visitor: TranspilerVisitor): String =
            IndentationQueue(strategy = visitor.indentationStrategy)
                    .apply { acceptTranspilation(q = this, visitor = visitor) }
                    .toInlineCode()

    /**
     * [acceptTranspilation] adds the transpiled code of the AST node in well formatted way to [q].
     * It does not need to worry about the x-character-limit issue, but it does need to consider
     * proper indentation.
     *
     * @param q the queue used to push indentation info.
     * @param visitor the visitor of the transpiler that actually does the transpiling logic.
     */
    fun acceptTranspilation(q: IndentationQueue, visitor: TranspilerVisitor)

}
