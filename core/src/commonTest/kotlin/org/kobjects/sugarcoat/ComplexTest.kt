package org.kobjects.sugarcoat

import org.kobjects.sugarcoat.datatype.F64Type
import org.kobjects.sugarcoat.parser.SugarcoatParser
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ComplexTest {


    val CODE = """

struct Complex
  a: F64
  b: F64
  
  fn abs() -> F64
    sqrt(self.a*self.a + self.b*self.b)
        
fn main()
  let c = Complex(1.0, 2.0)     
  print(c.b)    
"""

    @Test
    fun complexTest() {
        val program = SugarcoatParser.parseProgram(CODE)
        val result = StringBuilder()

        program.run {
            result.append(it)
        }

        assertEquals("2.0", result.toString())
    }

}