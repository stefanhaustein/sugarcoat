package org.kobjects.sugarcoat.fn

import org.kobjects.sugarcoat.ast.ParameterReference
import org.kobjects.sugarcoat.base.GlobalRuntimeContext
import org.kobjects.sugarcoat.base.Type
import org.kobjects.sugarcoat.base.Typed
import org.kobjects.sugarcoat.model.Classifier
import org.kobjects.sugarcoat.model.Instance

class LocalRuntimeContext(
    val globalRuntimeContext: GlobalRuntimeContext,
    val namespace: Classifier,
    val instance: Any?
) {
    val symbols = mutableMapOf<String, Any>()

    fun evalResolved(receiver: Any?, resolved: Any, children: List<ParameterReference>): Any {
        if (resolved is Callable) {
           return resolved.call(receiver, children, this)
        }
        require(children.isEmpty()) {
            "Can't apply parameters to $resolved"
        }
        return resolved
    }

    fun resolve(receiver: Any?, name: String): Pair<Any?, Any>? {
        if (name == "a") {
            println("ah!")
        }
        when (receiver) {
            null -> {
                val local = symbols[name]
                if (local != null) {
                    return null to local
                }
                val resolved = namespace.resolveOrNull(name)
                return if (resolved != null) (if (resolved is Callable && !resolved.static) instance else null) to resolved else null
            }

            is Classifier -> return null to receiver.resolve(name)
            is Instance -> {
                val resolved = (receiver.type.resolve() as Classifier).resolve(name)
                return receiver to resolved
            }

            else -> {
                val type = Type.of(receiver)
                if (type is Classifier) {
                    val resolved = type.resolveOrNull(name)
                    if (resolved != null) return receiver to resolved
                }
                return null
            }
        }
    }


    fun resolveType(receiver: Any?, name: String): Type {
        val resolved = resolve(receiver, name)?.second ?: throw IllegalArgumentException("Can't resolve $receiver.$name")
        return if (resolved is Callable) ((resolved as Typed).type as FunctionType).returnType else Type.of(resolved)
    }

    fun evalSymbol(receiver: Any?, name: String, children: List<ParameterReference>): Any {
        val resolved = resolve(receiver, name)

        return if (resolved == null) throw IllegalStateException("Unrecognized symbol: '$name'")
        else evalResolved(resolved.first, resolved.second, children)
    }

}