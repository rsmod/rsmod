package org.rsmod.plugins.cache.config.obj

import org.rsmod.plugins.types.NamedObject

public class ObjectTypeList(private val elements: Map<Int, ObjectType>) : Map<Int, ObjectType> by elements {

    public operator fun get(named: NamedObject): ObjectType {
        return elements.getValue(named.id)
    }
}
