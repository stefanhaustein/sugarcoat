package org.kobjects.sugarcoat.model

import org.kobjects.sugarcoat.ast.Expression
import org.kobjects.sugarcoat.type.Type

data class StaticFieldDefinition(
    val name: String,
    var explicitType: Type?,
    val unresolvedExpression: Expression
) {

}

