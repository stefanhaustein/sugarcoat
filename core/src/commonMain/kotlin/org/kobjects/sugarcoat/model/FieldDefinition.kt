package org.kobjects.sugarcoat.model

import org.kobjects.sugarcoat.base.Namespace
import org.kobjects.sugarcoat.ast.Expression
import org.kobjects.sugarcoat.base.Type

class FieldDefinition(
    override val parent: Namespace,
    override val name: String,
    val type: Type,
    val defaultExpression: Expression?
) : Namespace
