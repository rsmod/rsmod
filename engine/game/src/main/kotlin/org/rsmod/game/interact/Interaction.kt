package org.rsmod.game.interact

import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.PathingEntity
import org.rsmod.game.loc.BoundLocInfo
import org.rsmod.game.obj.Obj

public sealed class Interaction(
    public val opSlot: Int,
    public val hasOpTrigger: Boolean,
    public val hasApTrigger: Boolean,
    public var apRange: Int,
    public var persistent: Boolean,
    public var apRangeCalled: Boolean = false,
    public var interacted: Boolean = false,
) {
    override fun toString(): String =
        "Interaction(" +
            "opSlot=$opSlot, " +
            "hasOpTrigger=$hasOpTrigger, " +
            "hasApTrigger=$hasApTrigger, " +
            "apRange=$apRange, " +
            "persistent=$persistent, " +
            "apRangeCalled=$apRangeCalled, " +
            "interacted=$interacted" +
            ")"
}

public class InteractionLoc(
    public val target: BoundLocInfo,
    opSlot: Int,
    hasOpTrigger: Boolean,
    hasApTrigger: Boolean,
    startApRange: Int = PathingEntity.DEFAULT_AP_RANGE,
    persistent: Boolean = false,
) : Interaction(opSlot, hasOpTrigger, hasApTrigger, startApRange, persistent)

public class InteractionNpc(
    public val target: Npc,
    opSlot: Int,
    hasOpTrigger: Boolean,
    hasApTrigger: Boolean,
    startApRange: Int = PathingEntity.DEFAULT_AP_RANGE,
    persistent: Boolean = false,
) : Interaction(opSlot, hasOpTrigger, hasApTrigger, startApRange, persistent)

public class InteractionObj(
    public val target: Obj,
    opSlot: Int,
    hasOpTrigger: Boolean,
    hasApTrigger: Boolean,
    startApRange: Int = PathingEntity.DEFAULT_AP_RANGE,
    persistent: Boolean = false,
) : Interaction(opSlot, hasOpTrigger, hasApTrigger, startApRange, persistent)
