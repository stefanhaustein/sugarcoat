package org.kobjects.sugarcoat

import org.kobjects.sugarcoat.datatype.F64Type
import org.kobjects.sugarcoat.datatype.I64Type
import org.kobjects.sugarcoat.parser.ExpressionParser
import kotlin.test.Test
import kotlin.test.assertEquals

class ExpressionsTest {

    @Test
    fun testSimpleExpressions() {

        assertEquals(-4L, ExpressionParser.eval("4-4-4"))
        // assertEquals("Hello", ctx.eval("left(\"HelloWorld\", 5)"))
        assertEquals(4.0, ExpressionParser.eval("2.0**2.0"))
    }
}