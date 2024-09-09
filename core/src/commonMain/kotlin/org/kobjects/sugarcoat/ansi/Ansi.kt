package org.kobjects.sugarcoat.ansi



class Ansi {

    companion object {

        fun setColor(color: Color, bg: Boolean, rgb: Boolean = false): String {
            val fgbg = if (bg) "48" else "38"
            if (rgb) {
                return "${Char(27)}[$fgbg;2;${color.r};${color.g};${color.b}m"
            }
            val r6 = color.r / 43
            val g6 = color.g / 43
            val b6 = color.b / 43
            val code = if (r6 == g6 && g6 == b6) {
                val greyscale = 0.299 * color.r + 0.587 * color.g + 0.114 * color.b
                232 + (greyscale / 10.64).toInt()
            } else 16 + 36 * r6 + 6 * g6 + b6
            return "${Char(27)}[$fgbg;5;${code}m"
        }


        fun twoPixel(top: Color, bottom: Color, rgb: Boolean): String {
            return setColor(top, false, rgb) + setColor(bottom, true, rgb) + "▀"
        }


    }




}