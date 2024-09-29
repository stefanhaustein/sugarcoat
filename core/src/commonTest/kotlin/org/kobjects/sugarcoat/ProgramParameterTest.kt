package org.kobjects.sugarcoat

import org.kobjects.sugarcoat.datatype.F64Type
import org.kobjects.sugarcoat.parser.SugarcoatParser
import org.kobjects.sugarcoat.testsources.FIZZBUZZ_CASE
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ProgramParameterTest {

    val SQR = """
fn main(x: F64)
    x * x
"""



    @Test
    fun sqrTest() {
        val program = SugarcoatParser.parseProgram(SQR)

        assertEquals("fn main(x: F64)\n  x * x\n\n", program.serialize())
        assertEquals(4.0, program.run(2.0))
    }

}