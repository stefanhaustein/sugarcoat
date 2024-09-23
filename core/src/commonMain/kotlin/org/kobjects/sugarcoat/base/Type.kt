package org.kobjects.sugarcoat.base

import org.kobjects.sugarcoat.datatype.BoolType
import org.kobjects.sugarcoat.datatype.F64Type
import org.kobjects.sugarcoat.datatype.I64RangeType
import org.kobjects.sugarcoat.datatype.I64Type
import org.kobjects.sugarcoat.datatype.ListType
import org.kobjects.sugarcoat.datatype.StringType
import org.kobjects.sugarcoat.datatype.VoidType

interface Type {
    fun resolve(): ResolvedType


    companion object {
        fun of(value: Any) =
            when (value) {
                is Boolean -> BoolType
                is Double -> F64Type
                is List<*> -> ListType
                is Long -> I64Type
                is LongRange -> I64RangeType
                is String -> StringType
                is Unit -> VoidType
                else -> throw IllegalArgumentException("Type of value '$value' not supported.")
            }

    }
}