package org.sampl.ast.raw

/**
 * [CompilationUnit] represents the top level node in the compiler.
 * It refers to a source code file, which contains some [imports] and a class [clazz].
 * During compilation, this node is used only temporarily as a tool for dependency analysis.
 */
internal data class CompilationUnit(val imports: Set<String>, val clazz: Clazz)
