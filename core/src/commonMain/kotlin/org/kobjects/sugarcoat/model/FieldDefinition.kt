package org.kobjects.sugarcoat.model

import org.kobjects.sugarcoat.base.Element
import org.kobjects.sugarcoat.ast.Expression
import org.kobjects.sugarcoat.base.Type

class FieldDefinition(
    override val parent: Element,
    override val name: String,
    val type: Type,
    val defaultExpression: Expression?
) : Element {
    override fun serialize(sb: StringBuilder) {
        sb.append("$name: $type")
        if (defaultExpression != null) {
            sb.append(" = ")
            defaultExpression.stringify(sb, 0)
        }
        sb.append("\n")
    }
}
