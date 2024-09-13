package org.rsmod.api.player.ui

import org.rsmod.events.KeyedEvent
import org.rsmod.game.entity.Player
import org.rsmod.game.type.interf.IfSubType
import org.rsmod.game.ui.Component
import org.rsmod.game.ui.UserInterface

public data class IfOpenTop(val player: Player, val interf: UserInterface) : KeyedEvent {
    override val id: Long = interf.id.toLong()
}

public data class IfOpenSub(
    val player: Player,
    val interf: UserInterface,
    val target: Component,
    val subType: IfSubType,
) : KeyedEvent {
    override val id: Long = interf.id.toLong()
}

public data class IfCloseSub(val player: Player, val interf: UserInterface, val from: Component) :
    KeyedEvent {
    override val id: Long = interf.id.toLong()
}
