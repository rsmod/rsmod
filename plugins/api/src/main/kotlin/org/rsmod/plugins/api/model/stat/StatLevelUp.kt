package org.rsmod.plugins.api.model.stat

import org.rsmod.game.event.Event
import org.rsmod.game.model.stat.StatKey

data class StatLevelUp(
    val key: StatKey,
    val oldLevel: Int,
    val newLevel: Int,
    val experience: Double
) : Event
