package org.sampl.util

import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream
import org.sampl.antlr.PLLexer
import org.sampl.antlr.PLParser
import org.sampl.ast.common.FunctionCategory
import org.sampl.ast.common.Literal
import org.sampl.ast.raw.ClassFunctionMember
import org.sampl.ast.raw.Clazz
import org.sampl.ast.raw.CompilationUnit
import org.sampl.ast.raw.LiteralExpr
import org.sampl.ast.type.TypeExpr
import org.sampl.parser.CompilationUnitBuilder
import org.sampl.runtime.PrimitiveRuntimeLibrary
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.nio.charset.Charset

/**
 * [AntlrUtil] defines a set of utility functions related to interop with ANTLR.
 */
internal object AntlrUtil {

    /**
     * [InputStream.toCompilationUnit] tries to build the compilation unit from an input stream that
     * contains the source code.
     */
    @JvmStatic
    private fun InputStream.toCompilationUnit(): CompilationUnit {
        val inStream = ANTLRInputStream(this)
        val tokenStream = CommonTokenStream(PLLexer(inStream))
        val parser = PLParser(tokenStream)
        val unit = parser.compilationUnit()
        return CompilationUnitBuilder.visitCompilationUnit(unit)
    }

    /**
     * [createClassFromSource] tries to create a class from the source files in the given [code].
     */
    @JvmStatic
    fun createClassFromSource(code: String): Clazz {
        val input = ByteArrayInputStream(code.toByteArray(charset = Charset.defaultCharset()))
        return input.toCompilationUnit().clazz
    }

}
