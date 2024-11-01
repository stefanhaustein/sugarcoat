package org.kobjects.sugarcoat.ast

import org.kobjects.sugarcoat.type.Type

abstract class ResolvedExpression : Expression {

    override fun resolve(context: ResolutionContext, expectedType: Type?): Expression {
        require(expectedType == null || expectedType == getType()) {
            "Expression type '${getType()}' does not match the expected type '$expectedType' for: $this"
        }
        return this
    }
}