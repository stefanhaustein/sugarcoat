package org.kobjects.sugarcoat.ast

import org.kobjects.sugarcoat.base.Type
import org.kobjects.sugarcoat.datatype.ListType
import org.kobjects.sugarcoat.fn.LocalRuntimeContext

class ListExpression(val elements: List<Expression>) : Expression {

    override fun eval(context: LocalRuntimeContext) = List(elements.size) { elements[it].eval(context) }


    override fun resolve(expectedType: Type?) = ListExpression(elements.map{ it.resolve(null) } )

    override fun getType() = ListType
}