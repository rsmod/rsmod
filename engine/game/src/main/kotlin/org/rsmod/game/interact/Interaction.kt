package org.rsmod.game.interact

import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.PathingEntity
import org.rsmod.game.entity.npc.NpcUid
import org.rsmod.game.loc.BoundLocInfo
import org.rsmod.game.obj.Obj
import org.rsmod.game.type.comp.ComponentType
import org.rsmod.game.type.obj.ObjType

public sealed class Interaction(
    public val hasOpTrigger: Boolean,
    public val hasApTrigger: Boolean,
    public var apRange: Int,
    public var persistent: Boolean,
    public var apRangeCalled: Boolean = false,
    public var interacted: Boolean = false,
) {
    override fun toString(): String =
        "Interaction(" +
            "hasOpTrigger=$hasOpTrigger, " +
            "hasApTrigger=$hasApTrigger, " +
            "apRange=$apRange, " +
            "persistent=$persistent, " +
            "apRangeCalled=$apRangeCalled, " +
            "interacted=$interacted" +
            ")"
}

public sealed class InteractionLoc(
    public val target: BoundLocInfo,
    hasOpTrigger: Boolean,
    hasApTrigger: Boolean,
    startApRange: Int,
    persistent: Boolean,
) : Interaction(hasOpTrigger, hasApTrigger, startApRange, persistent) {
    override fun toString(): String =
        "InteractionLoc(" +
            "target=$target, " +
            "hasOpTrigger=$hasOpTrigger, " +
            "hasApTrigger=$hasApTrigger, " +
            "apRange=$apRange, " +
            "persistent=$persistent, " +
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
    persistent: Boolean = false,
) : InteractionLoc(target, hasOpTrigger, hasApTrigger, startApRange, persistent)

public class InteractionLocT(
    public val objType: ObjType?,
    public val component: ComponentType,
    public val comsub: Int,
    target: BoundLocInfo,
    hasOpTrigger: Boolean,
    hasApTrigger: Boolean,
    startApRange: Int = PathingEntity.DEFAULT_AP_RANGE,
    persistent: Boolean = false,
) : InteractionLoc(target, hasOpTrigger, hasApTrigger, startApRange, persistent)

public sealed class InteractionNpc(
    public val target: Npc,
    hasOpTrigger: Boolean,
    hasApTrigger: Boolean,
    startApRange: Int,
    persistent: Boolean,
) : Interaction(hasOpTrigger, hasApTrigger, startApRange, persistent) {
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
            "persistent=$persistent, " +
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
    persistent: Boolean = false,
) : InteractionNpc(target, hasOpTrigger, hasApTrigger, startApRange, persistent)

public class InteractionNpcT(
    public val objType: ObjType?,
    public val component: ComponentType,
    public val comsub: Int,
    target: Npc,
    hasOpTrigger: Boolean,
    hasApTrigger: Boolean,
    startApRange: Int = PathingEntity.DEFAULT_AP_RANGE,
    persistent: Boolean = false,
) : InteractionNpc(target, hasOpTrigger, hasApTrigger, startApRange, persistent)

public class InteractionObj(
    public val target: Obj,
    public val op: InteractionOp,
    hasOpTrigger: Boolean,
    hasApTrigger: Boolean,
    startApRange: Int = PathingEntity.DEFAULT_AP_RANGE,
    persistent: Boolean = false,
) : Interaction(hasOpTrigger, hasApTrigger, startApRange, persistent) {
    override fun toString(): String =
        "InteractionObj(" +
            "target=$target, " +
            "hasOpTrigger=$hasOpTrigger, " +
            "hasApTrigger=$hasApTrigger, " +
            "apRange=$apRange, " +
            "persistent=$persistent, " +
            "apRangeCalled=$apRangeCalled, " +
            "interacted=$interacted" +
            ")"
}
