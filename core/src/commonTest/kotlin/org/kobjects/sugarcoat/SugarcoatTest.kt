package org.kobjects.sugarcoat

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SugarcoatTest {

    val SQR = """
def main(x):
    x * x
"""

    val FIZZBUZZ = """
def main():
   for range(1, 20): x
     if x % 3 == 0: 
       print "Fizz"
       if x % 5 == 0:
         print "Buzz"
     .elif x % 5 == 0:
       print "Buzz"
     .else:
       print x
     print("\n")    
"""

    @Test
    fun sqrTest() {
        val program = SugarcoatParser.parseProgram(SQR)

        assertEquals("def main(x):\n  x * x\n", program.toString())
        assertEquals(4.0, program.run(2.0))
    }

    @Test
    fun fizzBuzzTest() {
        val program = SugarcoatParser.parseProgram(FIZZBUZZ)
        val result = StringBuilder()

        program.run {
            result.append(it)
        }

        assertTrue(result.startsWith("1.0\n2.0\nFizz"), "Unexpected result $result")
    }
}