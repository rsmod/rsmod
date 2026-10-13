package org.runerealm.content.generic.locs.ladders

import org.runerealm.api.config.refs.content
import org.runerealm.api.config.refs.params
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.script.onOpLoc1
import org.runerealm.game.type.loc.UnpackedLocType
import org.runerealm.game.type.seq.SeqType
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

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
