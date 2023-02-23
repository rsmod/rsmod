package org.rsmod.plugins.api.cache.type.enums.literal

import org.rsmod.plugins.types.NamedItem

public object EnumTypeNamedItem : EnumTypeBaseInt<NamedItem> {

    override fun decode(value: Int): NamedItem {
        return NamedItem(value)
    }
}
