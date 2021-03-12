package org.rsmod.game.event.impl

import org.rsmod.game.event.Event
import org.rsmod.game.model.stat.StatKey

data class StatLevelEvent(
    val key: StatKey,
    val oldLevel: Int,
    val newLevel: Int,
    val experience: Double
) : Event
