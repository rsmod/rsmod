package org.rsmod.api.npc

import org.rsmod.api.npc.interact.AiLocInteractions
import org.rsmod.api.npc.interact.AiNpcInteractions
import org.rsmod.api.npc.interact.AiObjInteractions
import org.rsmod.api.npc.interact.AiPlayerInteractions
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.Player
import org.rsmod.game.entity.npc.NpcMode
import org.rsmod.game.interact.InteractionOp
import org.rsmod.game.loc.BoundLocInfo
import org.rsmod.game.obj.Obj

public fun Npc.opPlayer1(target: Player, interactions: AiPlayerInteractions) {
    opMode(NpcMode.OpPlayer1)
    interactions.interactOp(this, target, InteractionOp.Op1)
}

public fun Npc.opPlayer2(target: Player, interactions: AiPlayerInteractions) {
    opMode(NpcMode.OpPlayer2)
    interactions.interactOp(this, target, InteractionOp.Op2)
}

public fun Npc.opPlayer3(target: Player, interactions: AiPlayerInteractions) {
    opMode(NpcMode.OpPlayer3)
    interactions.interactOp(this, target, InteractionOp.Op3)
}

public fun Npc.opPlayer4(target: Player, interactions: AiPlayerInteractions) {
    opMode(NpcMode.OpPlayer4)
    interactions.interactOp(this, target, InteractionOp.Op4)
}

public fun Npc.opPlayer5(target: Player, interactions: AiPlayerInteractions) {
    opMode(NpcMode.OpPlayer5)
    interactions.interactOp(this, target, InteractionOp.Op5)
}

public fun Npc.apPlayer1(target: Player, interactions: AiPlayerInteractions) {
    opMode(NpcMode.ApPlayer1)
    interactions.interactAp(this, target, InteractionOp.Op1)
}

public fun Npc.apPlayer2(target: Player, interactions: AiPlayerInteractions) {
    opMode(NpcMode.ApPlayer2)
    interactions.interactAp(this, target, InteractionOp.Op2)
}

public fun Npc.apPlayer3(target: Player, interactions: AiPlayerInteractions) {
    opMode(NpcMode.ApPlayer3)
    interactions.interactAp(this, target, InteractionOp.Op3)
}

public fun Npc.apPlayer4(target: Player, interactions: AiPlayerInteractions) {
    opMode(NpcMode.ApPlayer4)
    interactions.interactAp(this, target, InteractionOp.Op4)
}

public fun Npc.apPlayer5(target: Player, interactions: AiPlayerInteractions) {
    opMode(NpcMode.ApPlayer5)
    interactions.interactAp(this, target, InteractionOp.Op5)
}

public fun Npc.opNpc1(target: Npc, interactions: AiNpcInteractions) {
    opMode(NpcMode.OpNpc1)
    interactions.interactOp(this, target, InteractionOp.Op1)
}

public fun Npc.opNpc2(target: Npc, interactions: AiNpcInteractions) {
    opMode(NpcMode.OpNpc2)
    interactions.interactOp(this, target, InteractionOp.Op2)
}

public fun Npc.opNpc3(target: Npc, interactions: AiNpcInteractions) {
    opMode(NpcMode.OpNpc3)
    interactions.interactOp(this, target, InteractionOp.Op3)
}

public fun Npc.opNpc4(target: Npc, interactions: AiNpcInteractions) {
    opMode(NpcMode.OpNpc4)
    interactions.interactOp(this, target, InteractionOp.Op4)
}

public fun Npc.opNpc5(target: Npc, interactions: AiNpcInteractions) {
    opMode(NpcMode.OpNpc5)
    interactions.interactOp(this, target, InteractionOp.Op5)
}

public fun Npc.apNpc1(target: Npc, interactions: AiNpcInteractions) {
    opMode(NpcMode.ApNpc1)
    interactions.interactAp(this, target, InteractionOp.Op1)
}

public fun Npc.apNpc2(target: Npc, interactions: AiNpcInteractions) {
    opMode(NpcMode.ApNpc2)
    interactions.interactAp(this, target, InteractionOp.Op2)
}

public fun Npc.apNpc3(target: Npc, interactions: AiNpcInteractions) {
    opMode(NpcMode.ApNpc3)
    interactions.interactAp(this, target, InteractionOp.Op3)
}

public fun Npc.apNpc4(target: Npc, interactions: AiNpcInteractions) {
    opMode(NpcMode.ApNpc4)
    interactions.interactAp(this, target, InteractionOp.Op4)
}

public fun Npc.apNpc5(target: Npc, interactions: AiNpcInteractions) {
    opMode(NpcMode.ApNpc5)
    interactions.interactAp(this, target, InteractionOp.Op5)
}

public fun Npc.opLoc1(target: BoundLocInfo, interactions: AiLocInteractions) {
    opMode(NpcMode.OpLoc1)
    interactions.interactOp(this, target, InteractionOp.Op1)
}

public fun Npc.opLoc2(target: BoundLocInfo, interactions: AiLocInteractions) {
    opMode(NpcMode.OpLoc2)
    interactions.interactOp(this, target, InteractionOp.Op2)
}

public fun Npc.opLoc3(target: BoundLocInfo, interactions: AiLocInteractions) {
    opMode(NpcMode.OpLoc3)
    interactions.interactOp(this, target, InteractionOp.Op3)
}

public fun Npc.opLoc4(target: BoundLocInfo, interactions: AiLocInteractions) {
    opMode(NpcMode.OpLoc4)
    interactions.interactOp(this, target, InteractionOp.Op4)
}

public fun Npc.opLoc5(target: BoundLocInfo, interactions: AiLocInteractions) {
    opMode(NpcMode.OpLoc5)
    interactions.interactOp(this, target, InteractionOp.Op5)
}

public fun Npc.apLoc1(target: BoundLocInfo, interactions: AiLocInteractions) {
    opMode(NpcMode.ApLoc1)
    interactions.interactAp(this, target, InteractionOp.Op1)
}

public fun Npc.apLoc2(target: BoundLocInfo, interactions: AiLocInteractions) {
    opMode(NpcMode.ApLoc2)
    interactions.interactAp(this, target, InteractionOp.Op2)
}

public fun Npc.apLoc3(target: BoundLocInfo, interactions: AiLocInteractions) {
    opMode(NpcMode.ApLoc3)
    interactions.interactAp(this, target, InteractionOp.Op3)
}

public fun Npc.apLoc4(target: BoundLocInfo, interactions: AiLocInteractions) {
    opMode(NpcMode.ApLoc4)
    interactions.interactAp(this, target, InteractionOp.Op4)
}

public fun Npc.apLoc5(target: BoundLocInfo, interactions: AiLocInteractions) {
    opMode(NpcMode.ApLoc5)
    interactions.interactAp(this, target, InteractionOp.Op5)
}

public fun Npc.opObj1(target: Obj, interactions: AiObjInteractions) {
    opMode(NpcMode.OpObj1)
    interactions.interactOp(this, target, InteractionOp.Op1)
}

public fun Npc.opObj2(target: Obj, interactions: AiObjInteractions) {
    opMode(NpcMode.OpObj2)
    interactions.interactOp(this, target, InteractionOp.Op2)
}

public fun Npc.opObj3(target: Obj, interactions: AiObjInteractions) {
    opMode(NpcMode.OpObj3)
    interactions.interactOp(this, target, InteractionOp.Op3)
}

public fun Npc.opObj4(target: Obj, interactions: AiObjInteractions) {
    opMode(NpcMode.OpObj4)
    interactions.interactOp(this, target, InteractionOp.Op4)
}

public fun Npc.opObj5(target: Obj, interactions: AiObjInteractions) {
    opMode(NpcMode.OpObj5)
    interactions.interactOp(this, target, InteractionOp.Op5)
}

public fun Npc.apObj1(target: Obj, interactions: AiObjInteractions) {
    opMode(NpcMode.ApObj1)
    interactions.interactAp(this, target, InteractionOp.Op1)
}

public fun Npc.apObj2(target: Obj, interactions: AiObjInteractions) {
    opMode(NpcMode.ApObj2)
    interactions.interactAp(this, target, InteractionOp.Op2)
}

public fun Npc.apObj3(target: Obj, interactions: AiObjInteractions) {
    opMode(NpcMode.ApObj3)
    interactions.interactAp(this, target, InteractionOp.Op3)
}

public fun Npc.apObj4(target: Obj, interactions: AiObjInteractions) {
    opMode(NpcMode.ApObj4)
    interactions.interactAp(this, target, InteractionOp.Op4)
}

public fun Npc.apObj5(target: Obj, interactions: AiObjInteractions) {
    opMode(NpcMode.ApLoc5)
    interactions.interactAp(this, target, InteractionOp.Op5)
}

private fun Npc.opMode(mode: NpcMode) {
    this.mode = mode
}
