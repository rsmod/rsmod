package gg.rsmod.game.event.impl

import gg.rsmod.game.event.Event
import gg.rsmod.game.model.entity.Player

class LoginEvent(
    val player: Player,
    val stage: Stage
) : Event {

    sealed class Stage {
        object Priority : Stage()
        object Continue : Stage()
    }
}
