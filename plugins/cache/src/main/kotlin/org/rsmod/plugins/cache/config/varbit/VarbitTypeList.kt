package org.rsmod.plugins.cache.config.varbit

import org.rsmod.plugins.types.NamedVarbit

public class VarbitTypeList(private val elements: Map<Int, VarbitType>) : Map<Int, VarbitType> by elements {

    public operator fun get(named: NamedVarbit): VarbitType {
        return elements.getValue(named.id)
    }
}
