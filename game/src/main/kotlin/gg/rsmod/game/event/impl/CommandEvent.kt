package gg.rsmod.game.event.impl

import gg.rsmod.game.event.Event
import gg.rsmod.game.model.mob.Player

data class CommandEvent(
    val player: Player,
    val command: String,
    val args: List<String>
) : Event
