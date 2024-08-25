package org.kobjects.sugarcoat

import org.kobjects.sugarcoat.datatype.F64Type
import org.kobjects.sugarcoat.parser.ExpressionParser
import kotlin.test.Test
import kotlin.test.assertEquals

class ExpressionsTest {

    @Test
    fun testSimpleExpressions() {

        assertEquals(F64Type.Instance(-4.0), ExpressionParser.eval("4-4-4"))
        // assertEquals("Hello", ctx.eval("left(\"HelloWorld\", 5)"))
        assertEquals(F64Type.Instance(4.0), ExpressionParser.eval("2**2"))
    }
}