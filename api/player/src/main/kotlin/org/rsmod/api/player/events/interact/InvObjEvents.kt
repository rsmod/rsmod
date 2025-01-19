package org.rsmod.api.player.events.interact

import org.rsmod.events.KeyedEvent
import org.rsmod.events.UnboundEvent
import org.rsmod.game.entity.Player
import org.rsmod.game.obj.InvObj
import org.rsmod.game.type.droptrig.DropTriggerType
import org.rsmod.game.type.obj.UnpackedObjType
import org.rsmod.game.type.obj.Wearpos

public class InvObjDropEvents {
    public class Trigger(
        public val player: Player,
        public val dropSlot: Int,
        public val obj: InvObj,
        public val type: UnpackedObjType,
        triggerType: DropTriggerType,
    ) : KeyedEvent {
        override val id: Long = triggerType.id.toLong()
    }

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
}

public class InvEquipEvents {
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
        public val primaryObj: UnpackedObjType,
        public val secondaryObjs: Collection<UnpackedObjType?>,
    ) : UnboundEvent
}

public sealed class InvObjEvents : KeyedEvent {
    public class Op1(
        public val player: Player,
        public val slot: Int,
        public val obj: InvObj,
        public val type: UnpackedObjType,
        override val id: Long = obj.id.toLong(),
    ) : InvObjEvents()

    public class Op2(
        public val player: Player,
        public val slot: Int,
        public val obj: InvObj,
        public val type: UnpackedObjType,
        override val id: Long = obj.id.toLong(),
    ) : InvObjEvents()

    public class Op3(
        public val player: Player,
        public val slot: Int,
        public val obj: InvObj,
        public val type: UnpackedObjType,
        override val id: Long = obj.id.toLong(),
    ) : InvObjEvents()

    public class Op4(
        public val player: Player,
        public val slot: Int,
        public val obj: InvObj,
        public val type: UnpackedObjType,
        override val id: Long = obj.id.toLong(),
    ) : InvObjEvents()

    public class Op5(
        public val player: Player,
        public val slot: Int,
        public val obj: InvObj,
        public val type: UnpackedObjType,
        override val id: Long = obj.id.toLong(),
    ) : InvObjEvents()

    public class Op6(
        public val player: Player,
        public val slot: Int,
        public val obj: InvObj,
        public val type: UnpackedObjType,
        override val id: Long = obj.id.toLong(),
    ) : InvObjEvents()

    public class Op7(
        public val player: Player,
        public val slot: Int,
        public val obj: InvObj,
        public val type: UnpackedObjType,
        override val id: Long = obj.id.toLong(),
    ) : InvObjEvents()

    public class Op8(
        public val player: Player,
        public val slot: Int,
        public val obj: InvObj,
        public val type: UnpackedObjType,
        override val id: Long = obj.id.toLong(),
    ) : InvObjEvents()
}

public sealed class InvObjContentEvents : KeyedEvent {
    public class Op1(
        public val player: Player,
        public val slot: Int,
        public val obj: InvObj,
        public val type: UnpackedObjType,
        override val id: Long = type.contentGroup.toLong(),
    ) : InvObjContentEvents()

    public class Op2(
        public val player: Player,
        public val slot: Int,
        public val obj: InvObj,
        public val type: UnpackedObjType,
        override val id: Long = type.contentGroup.toLong(),
    ) : InvObjContentEvents()

    public class Op3(
        public val player: Player,
        public val slot: Int,
        public val obj: InvObj,
        public val type: UnpackedObjType,
        override val id: Long = type.contentGroup.toLong(),
    ) : InvObjContentEvents()

    public class Op4(
        public val player: Player,
        public val slot: Int,
        public val obj: InvObj,
        public val type: UnpackedObjType,
        override val id: Long = type.contentGroup.toLong(),
    ) : InvObjContentEvents()

    public class Op5(
        public val player: Player,
        public val slot: Int,
        public val obj: InvObj,
        public val type: UnpackedObjType,
        override val id: Long = type.contentGroup.toLong(),
    ) : InvObjContentEvents()

    public class Op6(
        public val player: Player,
        public val slot: Int,
        public val obj: InvObj,
        public val type: UnpackedObjType,
        override val id: Long = type.contentGroup.toLong(),
    ) : InvObjContentEvents()

    public class Op7(
        public val player: Player,
        public val slot: Int,
        public val obj: InvObj,
        public val type: UnpackedObjType,
        override val id: Long = type.contentGroup.toLong(),
    ) : InvObjContentEvents()

    public class Op8(
        public val player: Player,
        public val slot: Int,
        public val obj: InvObj,
        public val type: UnpackedObjType,
        override val id: Long = type.contentGroup.toLong(),
    ) : InvObjContentEvents()
}
