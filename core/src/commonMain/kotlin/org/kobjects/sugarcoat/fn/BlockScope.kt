package org.kobjects.sugarcoat.fn

import org.kobjects.sugarcoat.ast.Expression
import org.kobjects.sugarcoat.base.Type
import org.kobjects.sugarcoat.model.Classifier
import org.kobjects.sugarcoat.model.FieldDefinition
import org.kobjects.sugarcoat.model.StructInstance

fun BlockScope(parent: Classifier): BlockScope {
    return if (parent is FunctionDefinition) BlockScope(parent, parent)
      else if (parent is BlockScope) BlockScope(parent.parent, parent)
      else throw IllegalStateException()
}

class BlockScope(override val parent: FunctionDefinition, fallback: Classifier) : Classifier(parent, "", fallback) {

    override fun serialize(sb: StringBuilder) = throw UnsupportedOperationException()

    override fun toString() = "BlockScope for $parent"

    val locals = mutableMapOf<String, FieldDefinition>()

    init {
        parent.addChild(this)
        if (parent == fallback) {
            for (parameter in parent.parameters) {
                addField(parameter.name, parameter.type, null)
            }
        }
    }

    override fun addField(name: String, type: Type?, defaultExpression: Expression?) {
        locals[name] = FieldDefinition(name, type, defaultExpression)
    }

    override fun resolveTypes() {
        super.resolveTypes()

        if (!parent.static) {
            addControl( "self", parent.parent.selfType()) {
                    param, context -> context.instance!!
            }
        }

        val resolvedLocals = locals.values.map { it.resolve(this) }
        for (local in resolvedLocals) {
            locals[local.name] = local
            addControl(local.name, local.type!!) { _, context ->
                context.symbols[local.name] ?: throw IllegalStateException("'${local.name}' not found in ${context.symbols}")
            }
        }
    }


}