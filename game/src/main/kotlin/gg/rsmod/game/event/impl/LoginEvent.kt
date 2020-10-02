package gg.rsmod.game.event.impl

import gg.rsmod.game.event.Event
import gg.rsmod.game.model.mob.Player

class LoginEvent(
    val player: Player,
    val priority: Priority
) : Event {

    sealed class Priority {
        object High : Priority()
        object Normal : Priority()
        object Low : Priority()
    }
}
