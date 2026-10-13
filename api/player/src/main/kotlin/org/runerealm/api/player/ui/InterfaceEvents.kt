package org.runerealm.api.player.ui

import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.events.EventBus
import org.runerealm.events.KeyedEvent
import org.runerealm.events.SuspendEvent
import org.runerealm.game.entity.Player
import org.runerealm.game.type.comp.ComponentType
import org.runerealm.game.type.interf.IfButtonOp
import org.runerealm.game.type.interf.IfSubType
import org.runerealm.game.type.interf.InterfaceType
import org.runerealm.game.type.obj.UnpackedObjType
import org.runerealm.game.ui.Component
import org.runerealm.game.ui.UserInterface

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

/**
 * A sub-op selected from an _overlay_ component's right-click submenu (the client's `IF_SUBOP`
 * packet), e.g. "Rub" → "Edgeville" on an inventory amulet of glory.
 *
 * [op] is the parent interface op the submenu hangs off, encoded the same way as an
 * [IfOverlayButton.op], and [subop] is the 1-based sub-op id (the obj's `isubop` index + 1).
 */
public data class IfOverlaySubOp(
    val player: Player,
    val component: ComponentType,
    val comsub: Int,
    val obj: UnpackedObjType?,
    val op: IfButtonOp,
    val subop: Int,
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
