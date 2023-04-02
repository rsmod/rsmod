package org.rsmod.plugins.cache.config.param

import org.rsmod.plugins.types.NamedParameter

public class ParamTypeList(
    private val elements: Map<Int, ParamType<*>>
) : Map<Int, ParamType<*>> by elements {

    @Suppress("UNCHECKED_CAST")
    public operator fun <T> get(named: NamedParameter<T>): ParamType<T> {
        return elements.getValue(named.id) as ParamType<T>
    }
}
