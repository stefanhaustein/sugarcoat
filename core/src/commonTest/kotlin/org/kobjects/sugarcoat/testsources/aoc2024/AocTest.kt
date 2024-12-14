package org.kobjects.sugarcoat.testsources.aoc2024

import org.kobjects.sugarcoat.CodeWriter
import org.kobjects.sugarcoat.parser.SugarcoatParser
import org.kobjects.sugarcoat.testsources.COMPLEX_CASE
import org.kobjects.sugarcoat.testsources.FARM_CASE
import org.kobjects.sugarcoat.testsources.FIZZBUZZ_CASE
import kotlin.test.Test
import kotlin.test.assertEquals

class AocTest {

    @Test
    fun testDay00() = runTestCases(DAY_00_CASES)

    @Test
    fun testDay02() = runTestCases(DAY_02_CASES)

    fun runTestCases(cases: AocCases) {
        val source = cases.sourceCode

        val program = SugarcoatParser.parseProgram(source)

        val writer = CodeWriter()
        program.serialize(writer)
        println(writer.toString())

        for (case in cases.cases) {
            val result = program.run(case.first)
            assertEquals(case.second, result)
        }

    }

}