package org.sampl.blackbox

import org.junit.Test
import kotlin.concurrent.timerTask

/**
 * [BlackBoxTest] runs the black box tests.
 */
class BlackBoxTest {

    /**
     * A set of files for test cases without txt extension.
     */
    private val testCasesFiles: List<String> = listOf(
            "0-prop-types-proofs-programs",
            "1-multiple-features",
            "2-standard-runtime-sigs",
            "3-throw-exception",
            "4-stack-overflow",
            "5-standard-hello-world",
            "6-string-hello-world",
            "7-int-hello-world",
            // "8-tm-sim",
            "9-div-by-zero",
            "10-pattern-matching",
            "11-fib"
    )

    /**
     * Runs the test.
     */
    @Test
    fun test(): Unit = testCasesFiles
            .map { "$it.txt" }
            .map(transform = javaClass::getResourceAsStream)
            .map(transform = TestCaseProvider)
            .flatten()
            .forEach { case ->
                try {
                    case.run()
                } catch (e: Throwable) {
                    println("${case.name} failed!")
                    throw e
                }
            }

}
