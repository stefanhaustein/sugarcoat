package org.kobjects.sugarcoat.ast

import org.kobjects.sugarcoat.CodeWriter
import org.kobjects.sugarcoat.datatype.AnyType
import org.kobjects.sugarcoat.type.Type
import org.kobjects.sugarcoat.datatype.ListType
import org.kobjects.sugarcoat.fn.FunctionType
import org.kobjects.sugarcoat.fn.LocalRuntimeContext
import org.kobjects.sugarcoat.parser.Position
import org.kobjects.sugarcoat.type.GenericType

class ListExpression(position: Position, val elements: List<Expression>) : Expression(position) {

    override fun eval(context: LocalRuntimeContext) = List(elements.size) { elements[it].eval(context) }

    override fun serialize(writer: CodeWriter) {
        writer.append("[")
//        writer.indent()
        for ((index, child) in elements.withIndex()) {
            if (index > 0) {
                writer.append(",")
            }
  //          writer.newline()
            child.serialize(writer)
        }
        writer.append("]")
    //    writer.outdent()

    }


    override fun resolve(
        context: ResolutionContext,
        expectedType: Type?
    ): Expression {
        val elementType: Type?
        when (expectedType) {
            is ListType -> {
              elementType = expectedType.elementType
            }
            is FunctionType -> {
              return resolve(context, expectedType.returnType).asLambda(expectedType)
            }
            null,
            is GenericType -> {
                elementType = null
            }
            else -> throw IllegalStateException("$position: Can't convert list expression to type '$expectedType': $this")
        }
        return ListExpression(position, elements.map{ it.resolve(context, elementType) } )
    }

    override fun getType() = ListType(elements.firstOrNull()?.getType() ?: AnyType)
}