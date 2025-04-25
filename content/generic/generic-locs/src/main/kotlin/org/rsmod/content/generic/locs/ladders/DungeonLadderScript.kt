package org.rsmod.content.generic.locs.ladders

import org.rsmod.api.config.refs.content
import org.rsmod.api.config.refs.params
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpLoc1
import org.rsmod.game.type.loc.UnpackedLocType
import org.rsmod.game.type.seq.SeqType
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class DungeonLadderScript : PluginScript() {
    override fun ScriptContext.startup() {
        onOpLoc1(content.dungeonladder_down) { climbDown(it.type) }
        onOpLoc1(content.dungeonladder_up) { climbUp(it.type) }
    }

    private suspend fun ProtectedAccess.climbUp(type: UnpackedLocType): Unit = climb(type, -6400)

    private suspend fun ProtectedAccess.climbDown(type: UnpackedLocType): Unit = climb(type, 6400)

    private suspend fun ProtectedAccess.climb(type: UnpackedLocType, translateZ: Int) {
        arriveDelay()
        val dest = player.coords.translateZ(translateZ)
        anim(type.climbAnim())
        delay(1)
        telejump(dest)
    }

    private fun UnpackedLocType.climbAnim(): SeqType = param(params.climb_anim)
}
