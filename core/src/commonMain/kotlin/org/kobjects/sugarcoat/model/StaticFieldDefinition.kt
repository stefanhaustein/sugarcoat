package org.kobjects.sugarcoat.model

import org.kobjects.sugarcoat.ast.Expression
import org.kobjects.sugarcoat.type.Type

data class StaticFieldDefinition(
    val owner: Namespace,
    val mutable: Boolean,
    val name: String,
    val explicitType: Type?,
    var initializer: Expression
)

