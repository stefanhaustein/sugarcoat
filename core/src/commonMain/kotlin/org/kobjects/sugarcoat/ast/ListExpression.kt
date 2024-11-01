package org.kobjects.sugarcoat.ast

import org.kobjects.sugarcoat.type.Type
import org.kobjects.sugarcoat.datatype.ListType
import org.kobjects.sugarcoat.fn.LocalRuntimeContext

class ListExpression(val elements: List<Expression>) : Expression {

    override fun eval(context: LocalRuntimeContext) = List(elements.size) { elements[it].eval(context) }


    override fun resolve(
        context: ResolutionContext,
        expectedType: Type?
    ) = ListExpression(elements.map{ it.resolve(context, null) } )

    override fun getType() = ListType
}