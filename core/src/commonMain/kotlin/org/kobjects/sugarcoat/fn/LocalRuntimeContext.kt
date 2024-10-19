package org.kobjects.sugarcoat.fn

import org.kobjects.sugarcoat.ast.ParameterReference
import org.kobjects.sugarcoat.base.Element
import org.kobjects.sugarcoat.base.ControlStructures
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
        when (receiver) {
            null -> {
                val local = symbols[name]
                if (local != null) {
                    return null to local
                }
                if (instance is Instance) {
                    val field = instance.getField(name)
                    if (field != null) {
                        return instance to field
                    }
                }
                val resolved = namespace.resolveOrNull(name)
                return if (resolved != null) null to resolved else null
            }

            is Element -> return null to receiver.resolve(name)
            is Instance -> {
                val field = receiver.getField(name)
                if (field != null) {
                    return receiver to field
                }
                // TODO: This needs to move to impl
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
/*
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
            return ControlStructures.evalSymbol(name, children, this)
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
        if (type is Namespace) {
            val resolved = type.resolveOrNull(name)
            if (resolved != null) {
                return evalResolved(resolved, receiver, children)
            }
        }*/

        val resolved = resolve(receiver, name)

        return if (resolved == null) ControlStructures.evalSymbol(name, children, this)
        else evalResolved(resolved.first, resolved.second, children)
    }

}