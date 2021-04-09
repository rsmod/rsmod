package org.rsmod.game.event.impl

import org.rsmod.game.event.Event
import org.rsmod.game.model.mob.Npc
import org.rsmod.game.model.mob.Player
import org.rsmod.game.timer.TimerKey

data class PlayerTimerTrigger(
    val player: Player,
    val key: TimerKey
) : Event

data class NpcTimerTrigger(
    val npc: Npc,
    val key: TimerKey
) : Event
