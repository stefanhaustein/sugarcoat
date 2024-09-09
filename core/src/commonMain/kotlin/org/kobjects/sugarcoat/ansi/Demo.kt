package org.kobjects.sugarcoat.ansi

import kotlin.random.Random


fun main() {

    for (y in 0..20) {
        for (x in 0..72) {
            print(Ansi.twoPixel(
                Color.fromHsl(x * 4.9, 1.0, (y*2) / 41.0),
                Color.fromHsl(x * 4.9, 1.0, (y*2 + 1) / 41.0),
                true))
        }
        println(Ansi.setColor(Color.fromRgb(0,0,0), true))
    }
    println(Ansi.setColor(Color.fromRgb(255,255,255), false))
}