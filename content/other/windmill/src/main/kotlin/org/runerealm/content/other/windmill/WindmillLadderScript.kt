package org.runerealm.content.other.windmill

import org.runerealm.api.config.refs.seqs
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.script.onOpLoc1
import org.runerealm.api.script.onOpLoc2
import org.runerealm.api.script.onOpLoc3
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

class WindmillLadderScript : PluginScript() {
    override fun ScriptContext.startup() {
        onOpLoc1(windmill_locs.ladder_down) { climbDown() }
        onOpLoc1(windmill_locs.ladder_up) { climbUp() }
        onOpLoc1(windmill_locs.ladder_option) { climbOption() }
        onOpLoc2(windmill_locs.ladder_option) { climbUp() }
        onOpLoc3(windmill_locs.ladder_option) { climbDown() }
    }

    private suspend fun ProtectedAccess.climbUp(): Unit = climb(1)

    private suspend fun ProtectedAccess.climbDown(): Unit = climb(-1)

    private suspend fun ProtectedAccess.climb(translateLevel: Int) {
        arriveDelay()
        val dest = player.coords.translateLevel(translateLevel)
        anim(seqs.human_reachforladder)
        delay(1)
        telejump(dest)
    }

    private suspend fun ProtectedAccess.climbOption() {
        arriveDelay()
        startDialogue {
            val translate =
                choice2("Climb Up.", 1, "Climb Down.", -1, title = "Climb up or down the ladder?")
            val dest = player.coords.translateLevel(translate)
            anim(seqs.human_reachforladder)
            delay(2)
            telejump(dest)
        }
    }
}
