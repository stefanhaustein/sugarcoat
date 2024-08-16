package org.kobjects.sugarcoat

import kotlin.test.Test
import kotlin.test.assertEquals

class ExpressionsTest {

    @Test
    fun testSimpleExpressions() {

        assertEquals(DoubleContext(-4.0), ExpressionParser.eval("4-4-4"))
        // assertEquals("Hello", ctx.eval("left(\"HelloWorld\", 5)"))
        assertEquals(DoubleContext(4.0), ExpressionParser.eval("2**2"))
    }
}