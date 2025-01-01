package org.kobjects.sugarcoat.model

import org.kobjects.sugarcoat.ast.Expression
import org.kobjects.sugarcoat.type.Type

data class InstanceFieldDefinition(
    val name: String,
    var type: Type,
    val unresolvedDefaultExpression: Expression?
) {
    fun resolveType(context: Namespace) {
        type = type.resolveType(context)
    }
}

