package org.kobjects.sugarcoat.fn

import org.kobjects.sugarcoat.ast.Expression
import org.kobjects.sugarcoat.ast.ResolutionContext
import org.kobjects.sugarcoat.datatype.ListType
import org.kobjects.sugarcoat.type.Type
import org.kobjects.sugarcoat.model.Classifier

data class ParameterDefinition(
    val name: String,
    val type: Type,
    val repeated: Boolean = false,
    val defaultValue: Expression? = null,
) {
    override fun toString() = "$name: $type"

    fun resolveType(context: Classifier) = copy(type = type.resolve(context))

    fun resolveDefaultExpression(context: ResolutionContext) = copy(defaultValue = defaultValue?.resolve(context, type))

    fun restType(): Type = if (repeated) ListType(type) else type
}