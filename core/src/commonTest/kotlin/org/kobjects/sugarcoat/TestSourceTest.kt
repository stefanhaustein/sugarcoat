package org.kobjects.sugarcoat

import org.kobjects.sugarcoat.parser.SugarcoatParser
import org.kobjects.sugarcoat.testsources.COMPLEX_CASE
import org.kobjects.sugarcoat.testsources.FARM_CASE
import org.kobjects.sugarcoat.testsources.FIZZBUZZ_CASE
import org.kobjects.sugarcoat.testsources.STACK_CASE
import kotlin.test.Test
import kotlin.test.assertEquals

class TestSourceTest {

    @Test
    fun testComplex() = runCase(COMPLEX_CASE)

    @Test
    fun testFizzBuzz() = runCase(FIZZBUZZ_CASE)

    @Test
    fun testFarm() = runCase(FARM_CASE)


    @Test
    fun testStack() = runCase(STACK_CASE)


    fun runCase(case: Array<String>) {
        val name = case[0]
        val source = case[1]
        val expected = trim(case[2])

        val result = StringBuilder()
        val program = SugarcoatParser.parseProgram(source) { result.append(it) }

        val writer = CodeWriter()
        program.serialize(writer)
        println(writer.toString())

        if (expected != "") {
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