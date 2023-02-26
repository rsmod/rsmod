package org.rsmod.plugins.api.cache.type.literal

import org.rsmod.plugins.types.NamedItem

public object CacheTypeNamedItem : CacheTypeBaseInt<NamedItem> {

    override fun decode(value: Int): NamedItem {
        return NamedItem(value)
    }

    override fun encode(value: NamedItem): Int {
        return value.id
    }
}
