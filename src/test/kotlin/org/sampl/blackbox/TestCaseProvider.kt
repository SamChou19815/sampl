package org.sampl.blackbox

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

/**
 * [TestCaseProvider] is responsible for providing a list of test cases from an input stream.
 * It should throw an exception in case the test case is invalid.
 */
object TestCaseProvider : (InputStream) -> List<TestCase> {

    /**
     * [convert] converts the input stream to a [TestCase] without considering error handling and
     * closing resource.
     *
     * @throws IOException when there is some IO problem with the stream.
     */
    @Throws(IOException::class)
    private fun convert(inputStream: InputStream): List<TestCase> {
        val reader = BufferedReader(InputStreamReader(inputStream))
        val propMap = hashMapOf<String, String>()
        if (reader.readLine() != "/*") {
            error(message = "Bad Input! Need /* to start props.")
        }
        while (true) {
            val line = reader.readLine()
                    ?: error(message = "Bad Input! Need */ to end props.")
            if (line == "*/") {
                // Test Case Properties End
                break
            }
            // Record properties
            val twoParts = line.split(":").map { it.trim() }
            try {
                val key = twoParts[0]
                val value = twoParts[1]
                if (key in propMap) {
                    error(message = "Bad Input! Duplicate keys in props.")
                }
                propMap[key] = value
            } catch (e: ArrayIndexOutOfBoundsException) {
                error(message = "Bad Input! Incorrect prop line!")
            }
        }
        val name = propMap["name"] ?: error(message = "Bad Input. Missing name in props.")
        val types = propMap["types"]?.split(",")?.map { it.trim() }?.toSet()
                ?: error(message = "Bad Input. Missing types.")
        val codeBuilder = StringBuilder()
        while (true) {
            val line = reader.readLine() ?: break
            codeBuilder.append(line).append('\n')
        }
        val sourceCode = codeBuilder.toString()
        return types.map { type ->
            when (type) {
                "COMPILE_WITHOUT_ERROR" ->
                    TestCase.CompileWithoutError(name = name, sourceCode = sourceCode)
                "DOES_NOT_COMPILE" ->
                    TestCase.DoesNotCompile(name = name, sourceCode = sourceCode)
                "INTERPRETATION_WITH_EXPECTED_RESULT" ->
                    TestCase.InterpretationWithExpectedResult(
                            name = name, sourceCode = sourceCode,
                            expectedResult = propMap["expected-result"]
                                    ?: error("Bad Input! Missing expected-result in props.")
                    )
                "INTERPRETATION_WITH_EXPECTED_ERROR" ->
                    TestCase.InterpretationWithExpectedError(
                            name = name, sourceCode = sourceCode,
                            expectedError = propMap["expected-error"]
                                    ?: error("Bad Input! Missing expected-error in props.")
                    )
                "PIPELINE" -> TestCase.Pipeline(name = name, sourceCode = sourceCode)
                else -> error(message = "Unrecognized Type!")
            }
        }
    }

    /**
     * @see invoke
     */
    override fun invoke(p1: InputStream): List<TestCase> =
            try {
                convert(inputStream = p1)
            } catch (e: IOException) {
                error(message = "Unknown Error!")
            } finally {
                p1.close()
            }

}
