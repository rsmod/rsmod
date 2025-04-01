package org.rsmod.api.game.process.npc.mode

import jakarta.inject.Inject
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.npc.NpcMode
import org.rsmod.game.interact.InteractionOp

public class NpcModeProcessor
@Inject
constructor(
    private val wanderMode: NpcWanderModeProcessor,
    private val patrolMode: NpcPatrolModeProcessor,
    private val playerFaceCloseMode: NpcPlayerFaceCloseModeProcessor,
    private val playerFaceMode: NpcPlayerFaceModeProcessor,
    private val playerFollowMode: NpcPlayerFollowModeProcessor,
    private val playerEscapeMode: NpcPlayerEscapeModeProcessor,
    private val aiPlayerMode: AiPlayerModeProcessor,
    private val aiNpcMode: AiNpcModeProcessor,
    private val aiLocMode: AiLocModeProcessor,
    private val aiObjMode: AiObjModeProcessor,
) {
    public fun process(npc: Npc) {
        val mode = npc.mode ?: npc.defaultMode
        npc.mode = mode
        npc.processMode(mode)
    }

    private fun Npc.processMode(mode: NpcMode): Unit =
        when (mode) {
            NpcMode.None -> clearInteraction()
            NpcMode.Wander -> wanderMode.process(this)
            NpcMode.Patrol -> patrolMode.process(this)
            NpcMode.PlayerEscape -> playerEscapeMode.process(this)
            NpcMode.PlayerFollow -> playerFollowMode.process(this)
            NpcMode.PlayerFace -> playerFaceMode.process(this)
            NpcMode.PlayerFaceClose -> playerFaceCloseMode.process(this)
            NpcMode.OpPlayer1 -> aiPlayerMode.processOp(this, InteractionOp.Op1)
            NpcMode.OpPlayer2 -> aiPlayerMode.processOp(this, InteractionOp.Op2)
            NpcMode.OpPlayer3 -> aiPlayerMode.processOp(this, InteractionOp.Op3)
            NpcMode.OpPlayer4 -> aiPlayerMode.processOp(this, InteractionOp.Op4)
            NpcMode.OpPlayer5 -> aiPlayerMode.processOp(this, InteractionOp.Op5)
            NpcMode.OpPlayer6 -> TODO()
            NpcMode.OpPlayer7 -> TODO()
            NpcMode.OpPlayer8 -> TODO()
            NpcMode.OpNpc1 -> aiNpcMode.processOp(this, InteractionOp.Op1)
            NpcMode.OpNpc2 -> aiNpcMode.processOp(this, InteractionOp.Op2)
            NpcMode.OpNpc3 -> aiNpcMode.processOp(this, InteractionOp.Op3)
            NpcMode.OpNpc4 -> aiNpcMode.processOp(this, InteractionOp.Op4)
            NpcMode.OpNpc5 -> aiNpcMode.processOp(this, InteractionOp.Op5)
            NpcMode.OpLoc1 -> aiLocMode.processOp(this, InteractionOp.Op1)
            NpcMode.OpLoc2 -> aiLocMode.processOp(this, InteractionOp.Op2)
            NpcMode.OpLoc3 -> aiLocMode.processOp(this, InteractionOp.Op3)
            NpcMode.OpLoc4 -> aiLocMode.processOp(this, InteractionOp.Op4)
            NpcMode.OpLoc5 -> aiLocMode.processOp(this, InteractionOp.Op5)
            NpcMode.OpObj1 -> aiObjMode.processOp(this, InteractionOp.Op1)
            NpcMode.OpObj2 -> aiObjMode.processOp(this, InteractionOp.Op2)
            NpcMode.OpObj3 -> aiObjMode.processOp(this, InteractionOp.Op3)
            NpcMode.OpObj4 -> aiObjMode.processOp(this, InteractionOp.Op4)
            NpcMode.OpObj5 -> aiObjMode.processOp(this, InteractionOp.Op5)
            NpcMode.ApPlayer1 -> aiPlayerMode.processAp(this, InteractionOp.Op1)
            NpcMode.ApPlayer2 -> aiPlayerMode.processAp(this, InteractionOp.Op2)
            NpcMode.ApPlayer3 -> aiPlayerMode.processAp(this, InteractionOp.Op3)
            NpcMode.ApPlayer4 -> aiPlayerMode.processAp(this, InteractionOp.Op4)
            NpcMode.ApPlayer5 -> aiPlayerMode.processAp(this, InteractionOp.Op5)
            NpcMode.ApPlayer6 -> TODO()
            NpcMode.ApPlayer7 -> TODO()
            NpcMode.ApPlayer8 -> TODO()
            NpcMode.ApLoc1 -> aiLocMode.processAp(this, InteractionOp.Op1)
            NpcMode.ApLoc2 -> aiLocMode.processAp(this, InteractionOp.Op2)
            NpcMode.ApLoc3 -> aiLocMode.processAp(this, InteractionOp.Op3)
            NpcMode.ApLoc4 -> aiLocMode.processAp(this, InteractionOp.Op4)
            NpcMode.ApLoc5 -> aiLocMode.processAp(this, InteractionOp.Op5)
            NpcMode.ApNpc1 -> aiNpcMode.processAp(this, InteractionOp.Op1)
            NpcMode.ApNpc2 -> aiNpcMode.processAp(this, InteractionOp.Op2)
            NpcMode.ApNpc3 -> aiNpcMode.processAp(this, InteractionOp.Op3)
            NpcMode.ApNpc4 -> aiNpcMode.processAp(this, InteractionOp.Op4)
            NpcMode.ApNpc5 -> aiNpcMode.processAp(this, InteractionOp.Op5)
            NpcMode.ApObj1 -> aiObjMode.processAp(this, InteractionOp.Op1)
            NpcMode.ApObj2 -> aiObjMode.processAp(this, InteractionOp.Op2)
            NpcMode.ApObj3 -> aiObjMode.processAp(this, InteractionOp.Op3)
            NpcMode.ApObj4 -> aiObjMode.processAp(this, InteractionOp.Op4)
            NpcMode.ApObj5 -> aiObjMode.processAp(this, InteractionOp.Op5)
        }
}
