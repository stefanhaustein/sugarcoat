package org.kobjects.sugarcoat.fn

import org.kobjects.sugarcoat.ast.Expression
import org.kobjects.sugarcoat.base.Type
import org.kobjects.sugarcoat.model.Classifier
import org.kobjects.sugarcoat.model.FieldDefinition

fun BlockScope(parent: Classifier): BlockScope {
    return if (parent is FunctionDefinition) BlockScope(parent, parent)
      else if (parent is BlockScope) BlockScope(parent.parent, parent)
      else throw IllegalStateException()
}

class BlockScope(override val parent: FunctionDefinition, fallback: Classifier) : Classifier(parent, "", fallback) {

    override fun serialize(sb: StringBuilder) = throw UnsupportedOperationException()

    override fun toString() = "BlockScope for $parent"

    val locals = mutableMapOf<String, LocalDefinition>()

    init {
        if (parent == fallback) {
            for (parameter in parent.parameters) {
                addField(parameter.name, parameter.type, null)
            }
        }
    }

    override fun addField(name: String, type: Type, defaultExpression: Expression?) {
        locals[name] = LocalDefinition(name, type)
        addControl(name, type) { _, context ->
            context.symbols[name]!!
        }
    }


    data class LocalDefinition(val name: String, val Type: Type)
}