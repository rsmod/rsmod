package org.rsmod.plugins.cache.config.varp

import org.rsmod.plugins.types.NamedVarp

public class VarpTypeList(private val elements: Map<Int, VarpType>) : Map<Int, VarpType> by elements {

    public operator fun get(named: NamedVarp): VarpType {
        return elements.getValue(named.id)
    }
}
