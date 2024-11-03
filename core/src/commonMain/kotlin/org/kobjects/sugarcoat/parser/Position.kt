package org.kobjects.sugarcoat.parser

data class Position(val row: Int, val col: Int) {
    companion object {
        val INVALID = Position(-1, -1)
    }
}