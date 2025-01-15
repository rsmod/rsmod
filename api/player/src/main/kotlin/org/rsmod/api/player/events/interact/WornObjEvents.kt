package org.rsmod.api.player.events.interact

import org.rsmod.events.KeyedEvent
import org.rsmod.game.entity.Player
import org.rsmod.game.obj.InvObj
import org.rsmod.game.type.obj.UnpackedObjType

public sealed class WornObjEvents : KeyedEvent {
    public class Op2(
        public val player: Player,
        public val slot: Int,
        public val obj: InvObj,
        override val id: Long = obj.id.toLong(),
    ) : WornObjEvents()

    public class Op3(
        public val player: Player,
        public val slot: Int,
        public val obj: InvObj,
        override val id: Long = obj.id.toLong(),
    ) : WornObjEvents()

    public class Op4(
        public val player: Player,
        public val slot: Int,
        public val obj: InvObj,
        override val id: Long = obj.id.toLong(),
    ) : WornObjEvents()

    public class Op5(
        public val player: Player,
        public val slot: Int,
        public val obj: InvObj,
        override val id: Long = obj.id.toLong(),
    ) : WornObjEvents()

    public class Op6(
        public val player: Player,
        public val slot: Int,
        public val obj: InvObj,
        override val id: Long = obj.id.toLong(),
    ) : WornObjEvents()

    public class Op7(
        public val player: Player,
        public val slot: Int,
        public val obj: InvObj,
        override val id: Long = obj.id.toLong(),
    ) : WornObjEvents()

    public class Op8(
        public val player: Player,
        public val slot: Int,
        public val obj: InvObj,
        override val id: Long = obj.id.toLong(),
    ) : WornObjEvents()

    public class Op9(
        public val player: Player,
        public val slot: Int,
        public val obj: InvObj,
        override val id: Long = obj.id.toLong(),
    ) : WornObjEvents()
}

public sealed class WornObjContentEvents : KeyedEvent {
    public class Op2(
        public val player: Player,
        public val slot: Int,
        public val obj: InvObj,
        type: UnpackedObjType,
        override val id: Long = type.contentGroup.toLong(),
    ) : WornObjEvents()

    public class Op3(
        public val player: Player,
        public val slot: Int,
        public val obj: InvObj,
        type: UnpackedObjType,
        override val id: Long = type.contentGroup.toLong(),
    ) : WornObjEvents()

    public class Op4(
        public val player: Player,
        public val slot: Int,
        public val obj: InvObj,
        type: UnpackedObjType,
        override val id: Long = type.contentGroup.toLong(),
    ) : WornObjEvents()

    public class Op5(
        public val player: Player,
        public val slot: Int,
        public val obj: InvObj,
        type: UnpackedObjType,
        override val id: Long = type.contentGroup.toLong(),
    ) : WornObjEvents()

    public class Op6(
        public val player: Player,
        public val slot: Int,
        public val obj: InvObj,
        type: UnpackedObjType,
        override val id: Long = type.contentGroup.toLong(),
    ) : WornObjEvents()

    public class Op7(
        public val player: Player,
        public val slot: Int,
        public val obj: InvObj,
        type: UnpackedObjType,
        override val id: Long = type.contentGroup.toLong(),
    ) : WornObjEvents()

    public class Op8(
        public val player: Player,
        public val slot: Int,
        public val obj: InvObj,
        type: UnpackedObjType,
        override val id: Long = type.contentGroup.toLong(),
    ) : WornObjEvents()

    public class Op9(
        public val player: Player,
        public val slot: Int,
        public val obj: InvObj,
        type: UnpackedObjType,
        override val id: Long = type.contentGroup.toLong(),
    ) : WornObjEvents()
}
