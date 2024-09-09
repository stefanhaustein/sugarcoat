package org.kobjects.sugarcoat.ansi

import kotlin.math.abs

class Color(val rgb: Int) {

    val r: Int
        get() = rgb.shr(16).and(255)
    val g: Int
        get() = rgb.shr(8).and(255)
    val b: Int
        get() = rgb.and(255)

    companion object {
        fun fromRgb(r: Int, g: Int, b: Int) = Color(r.shl(16).or(g.shl(8).or(b)))
        fun fromRgb(r: Double, g: Double, b: Double) = fromRgb((255.0*r).toInt(), (255.0*g).toInt(), (255.0*b).toInt())

        fun fromHsl(h: Double, s: Double, l: Double): Color {
            var c = (1.0 - abs(2.0 * l - 1.0)) * s
            var x = c * (1 - abs((h / 60.0) % 2 - 1))
            val m = l - c/2.0

            c += m
            x += m

            return when ((h / 60.0).toInt()) {
                0 -> fromRgb(c, x, m)
                1 -> fromRgb(x, c, m)
                2 -> fromRgb(m, c, x)
                3 -> fromRgb(m, x, c)
                4 -> fromRgb(x, m, c)
                5 -> fromRgb(c, m, x)
                else -> throw IllegalArgumentException("hue $h out of range(0..<360)")
            }

        }

    }

}