package org.runerealm.api.player.events.interact

import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.events.EventBus
import org.runerealm.events.KeyedEvent
import org.runerealm.events.SuspendEvent
import org.runerealm.events.UnboundEvent
import org.runerealm.game.entity.Player
import org.runerealm.game.interact.HeldOp
import org.runerealm.game.inv.InvObj
import org.runerealm.game.inv.Inventory
import org.runerealm.game.type.droptrig.DropTriggerType
import org.runerealm.game.type.inv.InvType
import org.runerealm.game.type.obj.UnpackedObjType
import org.runerealm.game.type.obj.Wearpos

public class HeldBanksideEvents {
    public class Type(
        public val player: Player,
        public val slot: Int,
        public val type: UnpackedObjType,
        override val id: Long = type.id.toLong(),
    ) : KeyedEvent
}

public class HeldDropEvents {
    public class Trigger(
        public val player: Player,
        public val dropSlot: Int,
        public val obj: InvObj,
        public val type: UnpackedObjType,
        triggerType: DropTriggerType,
        override val id: Long = triggerType.id.toLong(),
    ) : KeyedEvent

    public data class Drop(
        public val player: Player,
        public val dropSlot: Int,
        public val obj: InvObj,
        public val type: UnpackedObjType,
    ) : UnboundEvent

    public data class Destroy(
        public val player: Player,
        public val invSlot: Int,
        public val obj: InvObj,
        public val type: UnpackedObjType,
    ) : UnboundEvent

    public data class Release(
        public val player: Player,
        public val invSlot: Int,
        public val obj: InvObj,
        public val type: UnpackedObjType,
    ) : UnboundEvent

    public data class Dispose(
        public val player: Player,
        public val invType: InvType,
        public val invSlot: Int,
        public val obj: InvObj,
    ) : UnboundEvent
}

public class HeldEquipEvents {
    public data class Equip(
        public val player: Player,
        public val invSlot: Int,
        public val wearpos: Wearpos,
        public val type: UnpackedObjType,
        override val id: Long = type.contentGroup.toLong(),
    ) : KeyedEvent

    public data class Unequip(
        public val player: Player,
        public val wearpos: Wearpos,
        public val type: UnpackedObjType,
        override val id: Long = type.contentGroup.toLong(),
    ) : KeyedEvent

    public data class WearposChange(
        public val player: Player,
        public val wearpos: Wearpos,
        public val objType: UnpackedObjType,
    ) : UnboundEvent
}

public sealed class HeldObjEvents(id: Number) : OpEvent(id.toLong()) {
    public class Op1(
        public val slot: Int,
        public val obj: InvObj,
        public val type: UnpackedObjType,
        public val inventory: Inventory,
    ) : HeldObjEvents(obj.id)

    public class Op2(
        public val slot: Int,
        public val obj: InvObj,
        public val type: UnpackedObjType,
        public val inventory: Inventory,
    ) : HeldObjEvents(obj.id)

    public class Op3(
        public val slot: Int,
        public val obj: InvObj,
        public val type: UnpackedObjType,
        public val inventory: Inventory,
    ) : HeldObjEvents(obj.id)

    public class Op4(
        public val slot: Int,
        public val obj: InvObj,
        public val type: UnpackedObjType,
        public val inventory: Inventory,
    ) : HeldObjEvents(obj.id)

    public class Op5(
        public val slot: Int,
        public val obj: InvObj,
        public val type: UnpackedObjType,
        public val inventory: Inventory,
    ) : HeldObjEvents(obj.id)

    /**
     * A sub-op picked from the right-click submenu of held [op] (the obj's `isubop` entries), e.g.
     * "Rub" → "Edgeville". [subop] is 1-based: `1` is the first entry of the submenu.
     */
    public class SubOp(
        public val slot: Int,
        public val obj: InvObj,
        public val type: UnpackedObjType,
        public val inventory: Inventory,
        public val op: HeldOp,
        public val subop: Int,
    ) : HeldObjEvents(obj.id)
}

public sealed class HeldContentEvents(id: Number) : OpEvent(id.toLong()) {
    public class Op1(
        public val slot: Int,
        public val obj: InvObj,
        public val type: UnpackedObjType,
        public val inventory: Inventory,
    ) : HeldContentEvents(type.contentGroup)

    public class Op2(
        public val slot: Int,
        public val obj: InvObj,
        public val type: UnpackedObjType,
        public val inventory: Inventory,
    ) : HeldContentEvents(type.contentGroup)

    public class Op3(
        public val slot: Int,
        public val obj: InvObj,
        public val type: UnpackedObjType,
        public val inventory: Inventory,
    ) : HeldContentEvents(type.contentGroup)

    public class Op4(
        public val slot: Int,
        public val obj: InvObj,
        public val type: UnpackedObjType,
        public val inventory: Inventory,
    ) : HeldContentEvents(type.contentGroup)

    public class Op5(
        public val slot: Int,
        public val obj: InvObj,
        public val type: UnpackedObjType,
        public val inventory: Inventory,
    ) : HeldContentEvents(type.contentGroup)
}

public class HeldUEvents {
    public class Type(
        public val first: UnpackedObjType,
        public val firstSlot: Int,
        public val second: UnpackedObjType,
        public val secondSlot: Int,
    ) : SuspendEvent<ProtectedAccess> {
        override val id: Long = EventBus.composeLongKey(first.id, second.id)
    }
}

public class HeldUContentEvents {
    public class Type(
        public val first: UnpackedObjType,
        public val firstSlot: Int,
        public val second: UnpackedObjType,
        public val secondSlot: Int,
    ) : SuspendEvent<ProtectedAccess> {
        override val id: Long = EventBus.composeLongKey(first.contentGroup, second.id)
    }

    public class Content(
        public val first: UnpackedObjType,
        public val firstSlot: Int,
        public val second: UnpackedObjType,
        public val secondSlot: Int,
    ) : SuspendEvent<ProtectedAccess> {
        override val id: Long = EventBus.composeLongKey(first.contentGroup, second.contentGroup)
    }
}

public class HeldUDefaultEvents {
    public class Type(
        public val first: UnpackedObjType,
        public val firstSlot: Int,
        public val second: UnpackedObjType,
        public val secondSlot: Int,
    ) : SuspendEvent<ProtectedAccess> {
        override val id: Long = first.id.toLong()
    }

    public class Content(
        public val first: UnpackedObjType,
        public val firstSlot: Int,
        public val second: UnpackedObjType,
        public val secondSlot: Int,
    ) : SuspendEvent<ProtectedAccess> {
        override val id: Long = first.contentGroup.toLong()
    }
}
