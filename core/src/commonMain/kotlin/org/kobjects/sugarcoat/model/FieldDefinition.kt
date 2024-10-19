package org.kobjects.sugarcoat.model

import org.kobjects.sugarcoat.ast.Expression
import org.kobjects.sugarcoat.base.Type

class FieldDefinition(
    override val parent: Classifier,
    override val name: String,
    val type: Type,
    val defaultExpression: Expression?
) : Classifier(parent, name) {
    override fun serialize(sb: StringBuilder) {
        sb.append("$name: $type")
        if (defaultExpression != null) {
            sb.append(" = ")
            defaultExpression.stringify(sb, 0)
        }
        sb.append("\n")
    }

    override fun toString(): String {
        val sb = StringBuilder()
        serialize(sb)
        return sb.toString()
    }
}
