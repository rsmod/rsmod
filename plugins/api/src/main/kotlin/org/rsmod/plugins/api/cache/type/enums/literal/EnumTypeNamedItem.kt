package org.rsmod.plugins.api.cache.type.enums.literal

import org.rsmod.game.types.NamedItem

public object EnumTypeNamedItem : EnumTypeBaseInt<NamedItem> {

    override fun decode(value: Int): NamedItem {
        return NamedItem(value)
    }
}
