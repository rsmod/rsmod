package gg.rsmod.game.event.impl

import gg.rsmod.game.event.Event
import gg.rsmod.game.model.mob.Player
import gg.rsmod.game.timer.TimerKey

data class PlayerTimerEvent(
    val player: Player,
    val key: TimerKey
) : Event
