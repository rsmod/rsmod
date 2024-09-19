package org.rsmod.api.game.process.npc.mode

import jakarta.inject.Inject
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.npc.NpcMode

public class NpcModeProcessor
@Inject
constructor(
    private val wanderMode: NpcWanderModeProcessor,
    private val patrolMode: NpcPatrolModeProcessor,
    private val playerFaceCloseMode: NpcPlayerFaceCloseModeProcessor,
    private val playerFaceMode: NpcPlayerFaceModeProcessor,
    private val playerFollowMode: NpcPlayerFollowModeProcessor,
    private val playerEscapeMode: NpcPlayerEscapeModeProcessor,
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
            NpcMode.OpPlayer1 -> TODO()
            NpcMode.OpPlayer2 -> TODO()
            NpcMode.OpPlayer3 -> TODO()
            NpcMode.OpPlayer4 -> TODO()
            NpcMode.OpPlayer5 -> TODO()
            NpcMode.OpPlayer6 -> TODO()
            NpcMode.OpPlayer7 -> TODO()
            NpcMode.OpPlayer8 -> TODO()
            NpcMode.ApPlayer1 -> TODO()
            NpcMode.ApPlayer2 -> TODO()
            NpcMode.ApPlayer3 -> TODO()
            NpcMode.ApPlayer4 -> TODO()
            NpcMode.ApPlayer5 -> TODO()
            NpcMode.ApPlayer6 -> TODO()
            NpcMode.ApPlayer7 -> TODO()
            NpcMode.ApPlayer8 -> TODO()
            NpcMode.OpNpc1 -> TODO()
            NpcMode.OpNpc2 -> TODO()
            NpcMode.OpNpc3 -> TODO()
            NpcMode.OpNpc4 -> TODO()
            NpcMode.OpNpc5 -> TODO()
            NpcMode.ApNpc1 -> TODO()
            NpcMode.ApNpc2 -> TODO()
            NpcMode.ApNpc3 -> TODO()
            NpcMode.ApNpc4 -> TODO()
            NpcMode.ApNpc5 -> TODO()
            NpcMode.OpLoc1 -> TODO()
            NpcMode.OpLoc2 -> TODO()
            NpcMode.OpLoc3 -> TODO()
            NpcMode.OpLoc4 -> TODO()
            NpcMode.OpLoc5 -> TODO()
            NpcMode.ApLoc1 -> TODO()
            NpcMode.ApLoc2 -> TODO()
            NpcMode.ApLoc3 -> TODO()
            NpcMode.ApLoc4 -> TODO()
            NpcMode.ApLoc5 -> TODO()
            NpcMode.OpObj1 -> TODO()
            NpcMode.OpObj2 -> TODO()
            NpcMode.OpObj3 -> TODO()
            NpcMode.OpObj4 -> TODO()
            NpcMode.OpObj5 -> TODO()
            NpcMode.ApObj1 -> TODO()
            NpcMode.ApObj2 -> TODO()
            NpcMode.ApObj3 -> TODO()
            NpcMode.ApObj4 -> TODO()
            NpcMode.ApObj5 -> TODO()
        }
}
