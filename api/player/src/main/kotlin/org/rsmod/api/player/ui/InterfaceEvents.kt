package org.rsmod.api.player.ui

import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.events.KeyedEvent
import org.rsmod.events.SuspendEvent
import org.rsmod.game.entity.Player
import org.rsmod.game.type.comp.ComponentType
import org.rsmod.game.type.interf.IfButtonOp
import org.rsmod.game.type.interf.IfSubType
import org.rsmod.game.type.obj.UnpackedObjType
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

public data class IfModalButton(
    val component: ComponentType,
    val comsub: Int,
    val obj: UnpackedObjType?,
    val op: IfButtonOp,
) : SuspendEvent<ProtectedAccess> {
    override val id: Long = component.packed.toLong()
}

public data class IfOverlayButton(
    val player: Player,
    val component: ComponentType,
    val comsub: Int,
    val obj: UnpackedObjType?,
    val op: IfButtonOp,
) : KeyedEvent {
    override val id: Long = component.packed.toLong()
}
