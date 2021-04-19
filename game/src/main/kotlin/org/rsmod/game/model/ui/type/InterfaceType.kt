package org.rsmod.game.model.ui.type

import org.rsmod.game.cache.type.CacheType
import org.rsmod.game.model.ui.Component

data class InterfaceType(override val id: Int, val children: List<Component>) : CacheType
