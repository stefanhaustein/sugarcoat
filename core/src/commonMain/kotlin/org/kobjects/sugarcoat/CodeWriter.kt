package org.kobjects.sugarcoat

class CodeWriter {
    val sb = StringBuilder()
    var indent = ""

    fun append(value: Any): CodeWriter {
        sb.append(value)
        return this
    }

    fun newline() {
        sb.append('\n').append(indent)
    }

    fun indent() {
        indent += "  "
    }

    fun outdent() {
        indent = indent.dropLast(2)
    }

    override fun toString() = sb.toString()
}