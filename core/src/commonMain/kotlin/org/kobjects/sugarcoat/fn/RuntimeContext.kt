package org.kobjects.sugarcoat.fn

import org.kobjects.sugarcoat.ast.ParameterReference
import org.kobjects.sugarcoat.base.Namespace
import org.kobjects.sugarcoat.base.RootContext
import org.kobjects.sugarcoat.base.Type
import org.kobjects.sugarcoat.model.Instance

class RuntimeContext(
    val namespace: Namespace,
    val instance: Any?
) {
    val symbols = mutableMapOf<String, Any>()

    fun evalResolved(resolved: Any, receiver: Any?, children: List<ParameterReference>): Any {
        if (resolved is Callable) {
           return resolved.call(receiver, children, this)
        }
        require(children.isEmpty()) {
            "Can't apply parameters to $resolved"
        }
        return resolved
    }

    fun evalSymbol(receiver: Any?, name: String, children: List<ParameterReference>): Any {

        if (receiver == null) {
            val local = symbols[name]
            if (local != null) {
                return evalResolved(local, null, children)
            }
            if (instance is Instance) {
                val field = instance.getField(name)
                if (field != null) {
                    return evalResolved(field, instance, children)
                }
            }
            val resolved = namespace.resolveOrNull(name)
            if (resolved != null) {
                return evalResolved(resolved, null, children)
            }
            return RootContext.evalSymbol(name, children, this)
        }

        if (receiver is Namespace) {
            val resolved = receiver.resolve(name)
            return evalResolved(resolved, null, children)
        }

        if (receiver is Instance) {
            val field = receiver.getField(name)
            if (field != null) {
                return evalResolved(field, receiver, children)
            }
            val resolved = receiver.type.resolveOrNull(name)
            require(resolved != null) {
                "Unable to resolve '$name'"
            }
            evalResolved(resolved, receiver, children)
        }

        val type = Type.of(receiver)
        val resolved = type.resolveOrNull(name)
        if (resolved != null) {
            return evalResolved(resolved, receiver, children)
        }
        return RootContext.evalSymbol(name, children, this)
    }

}