package org.rsmod.game.interact

import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.PathingEntity
import org.rsmod.game.entity.Player
import org.rsmod.game.entity.npc.NpcUid
import org.rsmod.game.entity.player.PlayerUid
import org.rsmod.game.loc.BoundLocInfo
import org.rsmod.game.obj.Obj
import org.rsmod.game.type.comp.ComponentType
import org.rsmod.game.type.obj.ObjType

public sealed class Interaction(
    public val hasOpTrigger: Boolean,
    public val hasApTrigger: Boolean,
    public var apRange: Int,
    public var apRangeCalled: Boolean = false,
    public var interacted: Boolean = false,
) {
    override fun toString(): String =
        "Interaction(" +
            "hasOpTrigger=$hasOpTrigger, " +
            "hasApTrigger=$hasApTrigger, " +
            "apRange=$apRange, " +
            "apRangeCalled=$apRangeCalled, " +
            "interacted=$interacted" +
            ")"
}

public sealed class InteractionLoc(
    public val target: BoundLocInfo,
    hasOpTrigger: Boolean,
    hasApTrigger: Boolean,
    startApRange: Int,
) : Interaction(hasOpTrigger, hasApTrigger, startApRange) {
    override fun toString(): String =
        "InteractionLoc(" +
            "target=$target, " +
            "hasOpTrigger=$hasOpTrigger, " +
            "hasApTrigger=$hasApTrigger, " +
            "apRange=$apRange, " +
            "apRangeCalled=$apRangeCalled, " +
            "interacted=$interacted" +
            ")"
}

public class InteractionLocOp(
    public val op: InteractionOp,
    target: BoundLocInfo,
    hasOpTrigger: Boolean,
    hasApTrigger: Boolean,
    startApRange: Int = PathingEntity.DEFAULT_AP_RANGE,
) : InteractionLoc(target, hasOpTrigger, hasApTrigger, startApRange)

public class InteractionLocT(
    public val objType: ObjType?,
    public val component: ComponentType,
    public val comsub: Int,
    target: BoundLocInfo,
    hasOpTrigger: Boolean,
    hasApTrigger: Boolean,
    startApRange: Int = PathingEntity.DEFAULT_AP_RANGE,
) : InteractionLoc(target, hasOpTrigger, hasApTrigger, startApRange)

public sealed class InteractionNpc(
    public val target: Npc,
    hasOpTrigger: Boolean,
    hasApTrigger: Boolean,
    startApRange: Int,
) : Interaction(hasOpTrigger, hasApTrigger, startApRange) {
    public val uid: NpcUid = target.uid

    init {
        check(uid != NpcUid.NULL) { "Npc does not have a proper uid: $target" }
    }

    override fun toString(): String =
        "InteractionNpc(" +
            "target=$target, " +
            "hasOpTrigger=$hasOpTrigger, " +
            "hasApTrigger=$hasApTrigger, " +
            "apRange=$apRange, " +
            "apRangeCalled=$apRangeCalled, " +
            "interacted=$interacted" +
            ")"
}

public class InteractionNpcOp(
    public val op: InteractionOp,
    target: Npc,
    hasOpTrigger: Boolean,
    hasApTrigger: Boolean,
    startApRange: Int = PathingEntity.DEFAULT_AP_RANGE,
) : InteractionNpc(target, hasOpTrigger, hasApTrigger, startApRange)

public class InteractionNpcT(
    public val objType: ObjType?,
    public val component: ComponentType,
    public val comsub: Int,
    target: Npc,
    hasOpTrigger: Boolean,
    hasApTrigger: Boolean,
    startApRange: Int = PathingEntity.DEFAULT_AP_RANGE,
) : InteractionNpc(target, hasOpTrigger, hasApTrigger, startApRange)

public class InteractionObj(
    public val target: Obj,
    public val op: InteractionOp,
    hasOpTrigger: Boolean,
    hasApTrigger: Boolean,
    startApRange: Int = PathingEntity.DEFAULT_AP_RANGE,
) : Interaction(hasOpTrigger, hasApTrigger, startApRange) {
    override fun toString(): String =
        "InteractionObj(" +
            "target=$target, " +
            "hasOpTrigger=$hasOpTrigger, " +
            "hasApTrigger=$hasApTrigger, " +
            "apRange=$apRange, " +
            "apRangeCalled=$apRangeCalled, " +
            "interacted=$interacted" +
            ")"
}

public sealed class InteractionPlayer(
    public val target: Player,
    hasOpTrigger: Boolean,
    hasApTrigger: Boolean,
    startApRange: Int,
) : Interaction(hasOpTrigger, hasApTrigger, startApRange) {
    public val uid: PlayerUid = target.uid

    init {
        check(uid != PlayerUid.NULL) { "Target does not have a proper uid: $target" }
    }

    override fun toString(): String =
        "InteractionPlayer(" +
            "target=$target, " +
            "hasOpTrigger=$hasOpTrigger, " +
            "hasApTrigger=$hasApTrigger, " +
            "apRange=$apRange, " +
            "apRangeCalled=$apRangeCalled, " +
            "interacted=$interacted" +
            ")"
}

public class InteractionPlayerOp(
    public val op: InteractionOp,
    target: Player,
    hasOpTrigger: Boolean,
    hasApTrigger: Boolean,
    startApRange: Int = PathingEntity.DEFAULT_AP_RANGE,
) : InteractionPlayer(target, hasOpTrigger, hasApTrigger, startApRange)

public class InteractionPlayerT(
    public val objType: ObjType?,
    public val component: ComponentType,
    public val comsub: Int,
    target: Player,
    hasOpTrigger: Boolean,
    hasApTrigger: Boolean,
    startApRange: Int = PathingEntity.DEFAULT_AP_RANGE,
) : InteractionPlayer(target, hasOpTrigger, hasApTrigger, startApRange)
