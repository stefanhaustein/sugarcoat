package org.kobjects.sugarcoat.ast

import org.kobjects.sugarcoat.CodeWriter
import org.kobjects.sugarcoat.fn.FunctionDefinition
import org.kobjects.sugarcoat.fn.LocalRuntimeContext
import org.kobjects.sugarcoat.type.Type

data class UnresolvedFunctionBody(val fn: FunctionDefinition) : Expression(fn.position) {

    override fun eval(context: LocalRuntimeContext): Any {
        throw UnsupportedOperationException(this.toString())
    }

    override fun serialize(writer: CodeWriter) {
        writer.append("<- Unresolved Function Body ->")
    }

    override fun resolve(
        context: ResolutionContext,
        expectedType: Type?
    ): Expression {
        throw UnsupportedOperationException(this.toString())
    }

    override fun getType(): Type {
        throw UnsupportedOperationException(this.toString())
    }
}