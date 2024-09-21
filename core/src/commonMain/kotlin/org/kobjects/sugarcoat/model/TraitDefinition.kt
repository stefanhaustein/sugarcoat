package org.kobjects.sugarcoat.model

import org.kobjects.sugarcoat.base.Definition
import org.kobjects.sugarcoat.base.ResolvedType

class TraitDefinition(
    parent: Definition,
    name: String
) : ResolvedType, AbstractClassifierDefinition(parent, name)