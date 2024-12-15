package org.kobjects.sugarcoat.parser

data class Position(val row: Int, val col: Int, val description: String? = null) {

    constructor(description: String) : this(-1, -1, description)

    companion object {
        val UNKNOWN = Position("?")
    }
}