package org.rsmod.api.game.process.npc.mode

import jakarta.inject.Inject
import org.rsmod.api.game.process.npc.NpcInteractionProcessor
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.npc.NpcMode
import org.rsmod.game.interact.Interaction

public class NpcModeProcessor
@Inject
constructor(
    private val wanderMode: NpcWanderModeProcessor,
    private val patrolMode: NpcPatrolModeProcessor,
    private val playerFaceCloseMode: NpcPlayerFaceCloseModeProcessor,
    private val playerFaceMode: NpcPlayerFaceModeProcessor,
    private val playerFollowMode: NpcPlayerFollowModeProcessor,
    private val playerEscapeMode: NpcPlayerEscapeModeProcessor,
    private val interactions: NpcInteractionProcessor,
) {
    public fun process(npc: Npc, interaction: Interaction?) {
        val mode = npc.mode ?: npc.defaultMode
        npc.mode = mode
        npc.processMode(mode, interaction)
    }

    private fun Npc.processMode(mode: NpcMode, interaction: Interaction?): Unit =
        when (mode) {
            NpcMode.None -> clearInteraction()
            NpcMode.Wander -> wanderMode.process(this)
            NpcMode.Patrol -> patrolMode.process(this)
            NpcMode.PlayerEscape -> playerEscapeMode.process(this)
            NpcMode.PlayerFollow -> playerFollowMode.process(this)
            NpcMode.PlayerFace -> playerFaceMode.process(this)
            NpcMode.PlayerFaceClose -> playerFaceCloseMode.process(this)
            NpcMode.OpPlayer1 -> processInteraction(interaction)
            NpcMode.OpPlayer2 -> processInteraction(interaction)
            NpcMode.OpPlayer3 -> processInteraction(interaction)
            NpcMode.OpPlayer4 -> processInteraction(interaction)
            NpcMode.OpPlayer5 -> processInteraction(interaction)
            NpcMode.OpPlayer6 -> processInteraction(interaction)
            NpcMode.OpPlayer7 -> processInteraction(interaction)
            NpcMode.OpPlayer8 -> processInteraction(interaction)
            NpcMode.ApPlayer1 -> processInteraction(interaction)
            NpcMode.ApPlayer2 -> processInteraction(interaction)
            NpcMode.ApPlayer3 -> processInteraction(interaction)
            NpcMode.ApPlayer4 -> processInteraction(interaction)
            NpcMode.ApPlayer5 -> processInteraction(interaction)
            NpcMode.ApPlayer6 -> processInteraction(interaction)
            NpcMode.ApPlayer7 -> processInteraction(interaction)
            NpcMode.ApPlayer8 -> processInteraction(interaction)
            NpcMode.OpNpc1 -> processInteraction(interaction)
            NpcMode.OpNpc2 -> processInteraction(interaction)
            NpcMode.OpNpc3 -> processInteraction(interaction)
            NpcMode.OpNpc4 -> processInteraction(interaction)
            NpcMode.OpNpc5 -> processInteraction(interaction)
            NpcMode.ApNpc1 -> processInteraction(interaction)
            NpcMode.ApNpc2 -> processInteraction(interaction)
            NpcMode.ApNpc3 -> processInteraction(interaction)
            NpcMode.ApNpc4 -> processInteraction(interaction)
            NpcMode.ApNpc5 -> processInteraction(interaction)
            NpcMode.OpLoc1 -> processInteraction(interaction)
            NpcMode.OpLoc2 -> processInteraction(interaction)
            NpcMode.OpLoc3 -> processInteraction(interaction)
            NpcMode.OpLoc4 -> processInteraction(interaction)
            NpcMode.OpLoc5 -> processInteraction(interaction)
            NpcMode.ApLoc1 -> processInteraction(interaction)
            NpcMode.ApLoc2 -> processInteraction(interaction)
            NpcMode.ApLoc3 -> processInteraction(interaction)
            NpcMode.ApLoc4 -> processInteraction(interaction)
            NpcMode.ApLoc5 -> processInteraction(interaction)
            NpcMode.OpObj1 -> processInteraction(interaction)
            NpcMode.OpObj2 -> processInteraction(interaction)
            NpcMode.OpObj3 -> processInteraction(interaction)
            NpcMode.OpObj4 -> processInteraction(interaction)
            NpcMode.OpObj5 -> processInteraction(interaction)
            NpcMode.ApObj1 -> processInteraction(interaction)
            NpcMode.ApObj2 -> processInteraction(interaction)
            NpcMode.ApObj3 -> processInteraction(interaction)
            NpcMode.ApObj4 -> processInteraction(interaction)
            NpcMode.ApObj5 -> processInteraction(interaction)
        }

    private fun Npc.processInteraction(interaction: Interaction?) {
        if (interaction == null) {
            resetMode()
            return
        }
        interactions.processPreMovement(this, interaction)
    }
}
