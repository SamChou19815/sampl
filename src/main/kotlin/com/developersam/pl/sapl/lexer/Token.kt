package com.developersam.pl.sapl.lexer

/**
 * [Token] is the class that at least records the line number of the token.
 * More detailed information will be reported by its subclasses.
 *
 * @param lineNumber line number where the token appears, used for reporting compile time or
 * run time error.
 */
sealed class Token(val lineNumber: Int)

/**
 * [KeywordToken] is the type of the token that does not contain any extra information other than
 * the type of the token.
 *
 * @param tokenType type of the token.
 * @param lineNumber line number where the token appears.
 */
class KeywordToken(val tokenType: TokenType, lineNumber: Int) : Token(lineNumber = lineNumber)

/**
 * [IdentifierToken] is the type of the token that stores a variable identifier.
 *
 * @param name name of the identifier.
 * @param lineNumber line number where the token appears.
 */
class IdentifierToken(val name: String, lineNumber: Int) : Token(lineNumber = lineNumber)

/**
 * [TypeToken] is the type of the token that stores a type of an expression.
 *
 * @param name name of the type of an expression.
 * @param lineNumber line number where the token appears.
 */
class TypeToken(val name: String, lineNumber: Int) : Token(lineNumber = lineNumber)

/**
 * [OperatorToken] is the type of the token that stores a customized operator.
 *
 * @param name name of the customized operator.
 * @param lineNumber line number where the token appears.
 */
class OperatorToken(val name: String, lineNumber: Int) : Token(lineNumber = lineNumber)

/**
 * [IntegerLiteralToken] is the type of the token that stores a long integer.
 *
 * @param value value of the long integer.
 * @param lineNumber line number where the token appears.
 */
class IntegerLiteralToken(val value: Long, lineNumber: Int) : Token(lineNumber = lineNumber)

/**
 * [FloatLiteralToken] is the type of the token that stores a long float.
 *
 * @param value value of the long float.
 * @param lineNumber line number where the token appears.
 */
class FloatLiteralToken(val value: Double, lineNumber: Int) : Token(lineNumber = lineNumber)

/**
 * [StringLiteralToken] is the type of the token that stores a string.
 *
 * @param value value of the string.
 * @param lineNumber line number where the token appears.
 */
class StringLiteralToken(val value: String, lineNumber: Int) : Token(lineNumber = lineNumber)
