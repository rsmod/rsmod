package org.rsmod.content.other.generic.ladders

import jakarta.inject.Inject
import org.rsmod.api.config.refs.content
import org.rsmod.api.config.refs.params
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpLoc1
import org.rsmod.game.type.loc.UnpackedLocType
import org.rsmod.game.type.seq.SeqType
import org.rsmod.pathfinder.collision.CollisionFlagMap
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class DungeonLadderScript @Inject constructor(private val collision: CollisionFlagMap) :
    PluginScript() {
    override fun ScriptContext.startUp() {
        onOpLoc1(content.dungeonladder_down) { climbDown(it.type) }
        onOpLoc1(content.dungeonladder_up) { climbUp(it.type) }
    }

    private suspend fun ProtectedAccess.climbUp(type: UnpackedLocType): Unit = climb(type, -6400)

    private suspend fun ProtectedAccess.climbDown(type: UnpackedLocType): Unit = climb(type, 6400)

    private suspend fun ProtectedAccess.climb(type: UnpackedLocType, translateZ: Int) {
        arriveDelay()
        val dest = player.coords.translateZ(translateZ)
        Ladders.climb(collision, this, dest, type.climbAnim())
    }

    private fun UnpackedLocType.climbAnim(): SeqType = param(params.climb_anim)
}
