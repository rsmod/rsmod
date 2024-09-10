package org.rsmod.content.other.generic.staircase

import jakarta.inject.Inject
import org.rsmod.api.config.refs.content
import org.rsmod.api.dialogue.Dialogues
import org.rsmod.api.dialogue.startDialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpLoc1
import org.rsmod.api.script.onOpLoc2
import org.rsmod.api.script.onOpLoc3
import org.rsmod.game.loc.BoundLocInfo
import org.rsmod.game.loc.LocAngle
import org.rsmod.map.util.Translation
import org.rsmod.pathfinder.collision.CollisionFlagMap
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class SpiralStaircaseScript
@Inject
constructor(private val collision: CollisionFlagMap, private val dialogues: Dialogues) :
    PluginScript() {
    override fun ScriptContext.startUp() {
        onOpLoc1(content.spiralstaircase_down) { climbDown(it.bound) }
        onOpLoc1(content.spiralstaircase_up) { climbUp(it.bound) }
        onOpLoc1(content.spiralstaircase_option) { climOption(it.bound) }
        onOpLoc2(content.spiralstaircase_option) {
            arriveDelay()
            climbUp(it.bound)
        }
        onOpLoc3(content.spiralstaircase_option) {
            arriveDelay()
            climbDown(it.bound)
        }
    }

    private fun ProtectedAccess.climbDown(loc: BoundLocInfo) =
        climb(loc, loc.climbDownTranslation())

    private fun BoundLocInfo.climbDownTranslation(): Translation =
        when (angle()) {
            LocAngle.West -> Translation(x = adjustedWidth - 1, z = -1, level = -1)
            LocAngle.North -> Translation(x = -adjustedWidth, z = 0, level = -1)
            LocAngle.East -> Translation(x = 0, z = adjustedLength, level = -1)
            LocAngle.South -> Translation(x = adjustedWidth, z = adjustedLength - 1, level = -1)
        }

    private fun ProtectedAccess.climbUp(loc: BoundLocInfo) = climb(loc, loc.climbUpTranslation())

    private fun BoundLocInfo.climbUpTranslation(): Translation =
        when (angle()) {
            LocAngle.West -> Translation(x = adjustedWidth, z = 0, level = 1)
            LocAngle.North -> Translation(x = 0, z = -(adjustedLength - 1), level = 1)
            LocAngle.East ->
                Translation(x = -(adjustedWidth - 1), z = adjustedLength - 1, level = 1)
            LocAngle.South -> Translation(x = adjustedWidth - 1, z = adjustedLength, level = 1)
        }

    private fun ProtectedAccess.climb(loc: BoundLocInfo, translation: Translation) {
        val dest = loc.coords.translate(translation)
        telejump(collision, dest)
    }

    private suspend fun ProtectedAccess.climOption(loc: BoundLocInfo) {
        startDialogue(dialogues) {
            val translation =
                choice2(
                    "Climb up the stairs.",
                    loc.climbUpTranslation(),
                    "Climb down the stairs.",
                    loc.climbDownTranslation(),
                    title = "Climb up or down the stairs?",
                )
            val dest = loc.coords.translate(translation)
            telejump(collision, dest)
        }
    }
}
