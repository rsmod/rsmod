package org.rsmod.plugins.api.cache.type.param

import org.rsmod.plugins.types.NamedParameter

public class ParamTypeList(
    private val elements: Map<Int, ParamType<*>>
) : Map<Int, ParamType<*>> by elements {

    @Suppress("UNCHECKED_CAST")
    public operator fun <T> get(named: NamedParameter<T>): ParamType<T> {
        return elements.getValue(named.id) as ParamType<T>
    }
}
