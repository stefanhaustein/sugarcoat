package org.kobjects.sugarcoat.type

import org.kobjects.sugarcoat.datatype.BoolType
import org.kobjects.sugarcoat.datatype.F64Type
import org.kobjects.sugarcoat.datatype.I64RangeType
import org.kobjects.sugarcoat.datatype.I64Type
import org.kobjects.sugarcoat.datatype.StringType
import org.kobjects.sugarcoat.datatype.VoidType
import org.kobjects.sugarcoat.model.Classifier
import org.kobjects.sugarcoat.model.StructDefinition
import org.kobjects.sugarcoat.model.TraitDefinition

interface Type {

    fun resolve(context: Classifier): Type = this

    fun assignableFrom(other: Type) = this == other

    fun resolveGenerics(state: GenericTypeResolverState, expected: Type? = null): Type? {
        /*require (other == null || other == this) {
             "${state.errorPrefix()}: Can't resolve type '$this' to '$other'"
        }*/
        return expected ?: this
    }

    companion object {
        fun of(value: Any) =
            when (value) {
                is Boolean -> BoolType
                is Double -> F64Type
              //  is List<*> -> ListType(AnyType)
                is Long -> I64Type
                is LongRange -> I64RangeType
                is String -> StringType
                is Typed -> value.type
                is Unit -> VoidType
                is StructDefinition -> MetaType(value)
                is TraitDefinition -> MetaType(value)
                else -> throw IllegalArgumentException("Type of value '$value' not supported.")
            }

    }
}