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
            NpcMode.OpPlayer1 -> aiPlayerMode.process(this, InteractionOp.Op1)
            NpcMode.OpPlayer2 -> aiPlayerMode.process(this, InteractionOp.Op2)
            NpcMode.OpPlayer3 -> aiPlayerMode.process(this, InteractionOp.Op3)
            NpcMode.OpPlayer4 -> aiPlayerMode.process(this, InteractionOp.Op4)
            NpcMode.OpPlayer5 -> aiPlayerMode.process(this, InteractionOp.Op5)
            NpcMode.OpNpc1 -> aiNpcMode.process(this, InteractionOp.Op1)
            NpcMode.OpNpc2 -> aiNpcMode.process(this, InteractionOp.Op2)
            NpcMode.OpNpc3 -> aiNpcMode.process(this, InteractionOp.Op3)
            NpcMode.OpNpc4 -> aiNpcMode.process(this, InteractionOp.Op4)
            NpcMode.OpNpc5 -> aiNpcMode.process(this, InteractionOp.Op5)
            NpcMode.OpLoc1 -> aiLocMode.process(this, InteractionOp.Op1)
            NpcMode.OpLoc2 -> aiLocMode.process(this, InteractionOp.Op2)
            NpcMode.OpLoc3 -> aiLocMode.process(this, InteractionOp.Op3)
            NpcMode.OpLoc4 -> aiLocMode.process(this, InteractionOp.Op4)
            NpcMode.OpLoc5 -> aiLocMode.process(this, InteractionOp.Op5)
            NpcMode.OpObj1 -> aiObjMode.process(this, InteractionOp.Op1)
            NpcMode.OpObj2 -> aiObjMode.process(this, InteractionOp.Op2)
            NpcMode.OpObj3 -> aiObjMode.process(this, InteractionOp.Op3)
            NpcMode.OpObj4 -> aiObjMode.process(this, InteractionOp.Op4)
            NpcMode.OpObj5 -> aiObjMode.process(this, InteractionOp.Op5)
        }
}
