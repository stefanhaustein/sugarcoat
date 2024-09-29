package org.kobjects.sugarcoat.model

import org.kobjects.sugarcoat.base.Namespace
import org.kobjects.sugarcoat.ast.Expression
import org.kobjects.sugarcoat.base.Type

class FieldDefinition(
    override val parent: Namespace,
    override val name: String,
    val type: Type,
    val defaultExpression: Expression?
) : Namespace {
    override fun serialize(sb: StringBuilder) {
        sb.append("$name: $type")
        if (defaultExpression != null) {
            sb.append(" = ")
            defaultExpression.stringify(sb, 0)
        }
        sb.append("\n")
    }
}
