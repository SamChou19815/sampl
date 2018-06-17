package org.sampl.util

import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream
import org.sampl.antlr.PLLexer
import org.sampl.antlr.PLParser
import org.sampl.ast.raw.RawProgram
import org.sampl.parser.ClassMemberBuilder
import java.io.ByteArrayInputStream

/**
 * [createRawProgramFromSource] tries to create a raw program from the source files in the given
 * [code].
 */
internal fun createRawProgramFromSource(code: String): RawProgram =
        code.toByteArray().let { ByteArrayInputStream(it) }
                .let { ANTLRInputStream(it) }
                .let { PLLexer(it) }
                .let { CommonTokenStream(it) }
                .let { PLParser(it) }
                .program().classMemberDeclaration()
                .map { it.accept(ClassMemberBuilder) }
                .let { RawProgram(members = it) }
