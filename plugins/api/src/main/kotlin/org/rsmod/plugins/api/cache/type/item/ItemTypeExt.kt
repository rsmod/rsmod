package org.rsmod.plugins.api.cache.type.item

import org.rsmod.game.model.item.type.ItemType

val ItemType.isNoted: Boolean
    get() = noteValue > 0

val ItemType.canBeNoted: Boolean
    get() = noteValue == 0 && noteLink > 0
