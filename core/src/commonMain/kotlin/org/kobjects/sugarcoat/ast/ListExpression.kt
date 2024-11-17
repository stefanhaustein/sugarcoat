package org.kobjects.sugarcoat.ast

import org.kobjects.sugarcoat.datatype.AnyType
import org.kobjects.sugarcoat.type.Type
import org.kobjects.sugarcoat.datatype.ListType
import org.kobjects.sugarcoat.fn.LocalRuntimeContext
import org.kobjects.sugarcoat.parser.Position

class ListExpression(position: Position, val elements: List<Expression>) : Expression(position) {

    override fun eval(context: LocalRuntimeContext) = List(elements.size) { elements[it].eval(context) }


    override fun resolve(
        context: ResolutionContext,
        expectedType: Type?
    ) = ListExpression(position, elements.map{ it.resolve(context, null) } )

    override fun getType() = ListType(elements.firstOrNull()?.getType() ?: AnyType)
}