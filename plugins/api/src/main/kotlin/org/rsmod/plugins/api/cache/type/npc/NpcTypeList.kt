package org.rsmod.plugins.api.cache.type.npc

import org.rsmod.plugins.api.cache.type.varp.VarpType
import org.rsmod.plugins.types.NamedNpc
import org.rsmod.plugins.types.NamedVarp

public class NpcTypeList(private val elements: Map<Int, NpcType>) : Map<Int, NpcType> by elements {

    public operator fun get(named: NamedNpc): NpcType {
        return elements.getValue(named.id)
    }
}
