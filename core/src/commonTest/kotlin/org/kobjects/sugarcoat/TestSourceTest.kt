package org.kobjects.sugarcoat

import org.kobjects.sugarcoat.parser.SugarcoatParser
import org.kobjects.sugarcoat.testsources.COMPLEX_CASE
import org.kobjects.sugarcoat.testsources.FARM_CASE
import org.kobjects.sugarcoat.testsources.FIZZBUZZ_CASE
import kotlin.test.Test
import kotlin.test.assertEquals

class TestSourceTest {

    val CASES = arrayOf(
        COMPLEX_CASE,
        FIZZBUZZ_CASE,
    //    FARM_CASE
    )

    @Test
    fun testSources() {
        for (case in CASES) {
            val name = case[0]
            val source = case[1]
            val expected = trim(case[2])

            val result = StringBuilder()
            val program = SugarcoatParser.parseProgram(source) { result.append(it) }

            program.run()
            val actual = trim(result.toString())

            assertEquals(expected, actual, "Unexpected result for $name.")
        }
    }

    companion object {
        fun trim(s: String): String {
            var start = 0
            while (start < s.length && s[start].code <= 32) {
                start++
            }
            var end = s.length
            while (end > start && s[end - 1].code <= 32) {
                end--
            }
            return s.substring(start, end)

        }
    }
}