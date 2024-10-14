package org.kobjects.sugarcoat.model

import org.kobjects.sugarcoat.base.Typed

interface Instance : Typed {
    override val type : Classifier

    fun getField(name: String): Any? = null
}