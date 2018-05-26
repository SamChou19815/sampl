@file:JvmName(name = "LexerUtil")

package com.developersam.pl.sapl.util

import com.developersam.pl.sapl.antlr.PLLexer
import org.antlr.v4.runtime.tree.TerminalNode

/**
 * [TerminalNode.symbolicName] returns the symbolic name of the terminal node.
 */
val TerminalNode.symbolicName: String
    get() = PLLexer.VOCABULARY.getSymbolicName(this.symbol.type)
