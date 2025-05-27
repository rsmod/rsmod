package org.rsmod.api.player.ui

import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.events.EventBus
import org.rsmod.events.KeyedEvent
import org.rsmod.events.SuspendEvent
import org.rsmod.game.entity.Player
import org.rsmod.game.type.comp.ComponentType
import org.rsmod.game.type.interf.IfButtonOp
import org.rsmod.game.type.interf.IfSubType
import org.rsmod.game.type.interf.InterfaceType
import org.rsmod.game.type.obj.UnpackedObjType
import org.rsmod.game.ui.Component
import org.rsmod.game.ui.UserInterface

public class IfMoveTop(public val player: Player, interf: InterfaceType) : KeyedEvent {
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

public class IfMoveSub(public val player: Player, destComponent: Int) : KeyedEvent {
    override val id: Long = destComponent.toLong()
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

public class IfModalButtonT(
    public val selectedSlot: Int,
    public val selectedObj: UnpackedObjType?,
    public val targetSlot: Int,
    public val targetObj: UnpackedObjType?,
    selectedComponent: Component,
    targetComponent: Component,
) : SuspendEvent<ProtectedAccess> {
    override val id: Long =
        EventBus.composeLongKey(selectedComponent.packed, targetComponent.packed)

    override fun toString(): String =
        "IfModalButtonT(" +
            "selectedSlot=$selectedSlot, " +
            "targetSlot=$targetSlot, " +
            "selectedObj=$selectedObj, " +
            "targetObj=$targetObj" +
            ")"
}

public class IfOverlayButtonT(
    public val player: Player,
    public val selectedSlot: Int,
    public val selectedObj: UnpackedObjType?,
    public val targetSlot: Int,
    public val targetObj: UnpackedObjType?,
    selectedComponent: Component,
    targetComponent: Component,
) : KeyedEvent {
    override val id: Long =
        EventBus.composeLongKey(selectedComponent.packed, targetComponent.packed)

    override fun toString(): String =
        "IfOverlayButtonT(" +
            "selectedSlot=$selectedSlot, " +
            "targetSlot=$targetSlot, " +
            "selectedObj=$selectedObj, " +
            "targetObj=$targetObj, " +
            "player=$player" +
            ")"
}

public class IfModalDrag(
    public val selectedSlot: Int?,
    public val selectedObj: Int?,
    public val targetSlot: Int?,
    public val targetObj: Int?,
    selectedComponent: Component,
    targetComponent: Component,
) : SuspendEvent<ProtectedAccess> {
    override val id: Long =
        EventBus.composeLongKey(selectedComponent.packed, targetComponent.packed)

    override fun toString(): String =
        "IfModalDrag(" +
            "selectedSlot=$selectedSlot, " +
            "targetSlot=$targetSlot, " +
            "selectedObj=$selectedObj, " +
            "targetObj=$targetObj" +
            ")"
}

public class IfOverlayDrag(
    public val player: Player,
    public val selectedSlot: Int?,
    public val selectedObj: Int?,
    public val targetSlot: Int?,
    public val targetObj: Int?,
    selectedComponent: Component,
    targetComponent: Component,
) : KeyedEvent {
    override val id: Long =
        EventBus.composeLongKey(selectedComponent.packed, targetComponent.packed)

    override fun toString(): String =
        "IfOverlayDrag(" +
            "selectedSlot=$selectedSlot, " +
            "targetSlot=$targetSlot, " +
            "selectedObj=$selectedObj, " +
            "targetObj=$targetObj, " +
            "player=$player" +
            ")"
}
