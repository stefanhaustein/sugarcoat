package org.kobjects.sugarcoat

import org.kobjects.sugarcoat.parser.SugarcoatParser
import org.kobjects.sugarcoat.testsources.RAYTRACER_SOURCE
import kotlin.test.Test

class RaytracerTest {

   @Test
   fun parsingTest() {
       val parsed = SugarcoatParser.parseProgram(RAYTRACER_SOURCE)
       parsed.run()
   }
}