package org.rsmod.plugins.api.cache.type.literal.codec

import org.rsmod.plugins.types.NamedObject

public object CacheTypeNamedObject : CacheTypeBaseInt<NamedObject>(NamedObject::class.java) {

    override fun decode(value: Int): NamedObject {
        return NamedObject(value)
    }

    override fun encode(value: NamedObject): Int {
        return value.id
    }
}
