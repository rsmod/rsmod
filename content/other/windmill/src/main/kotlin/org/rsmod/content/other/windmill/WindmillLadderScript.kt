package org.rsmod.content.other.windmill

import jakarta.inject.Inject
import org.rsmod.api.config.refs.seqs
import org.rsmod.api.player.dialogue.Dialogues
import org.rsmod.api.player.dialogue.startDialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpLoc1
import org.rsmod.api.script.onOpLoc2
import org.rsmod.api.script.onOpLoc3
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class WindmillLadderScript @Inject constructor(private val dialogues: Dialogues) : PluginScript() {
    override fun ScriptContext.startUp() {
        onOpLoc1(WindmillLocs.ladder_down) { climbDown() }
        onOpLoc1(WindmillLocs.ladder_up) { climbUp() }
        onOpLoc1(WindmillLocs.ladder_option) { climbOption() }
        onOpLoc2(WindmillLocs.ladder_option) { climbUp() }
        onOpLoc3(WindmillLocs.ladder_option) { climbDown() }
    }

    private suspend fun ProtectedAccess.climbUp(): Unit = climb(1)

    private suspend fun ProtectedAccess.climbDown(): Unit = climb(-1)

    private suspend fun ProtectedAccess.climb(translateLevel: Int) {
        arriveDelay()
        val dest = player.coords.translateLevel(translateLevel)
        anim(seqs.human_reachforladddertop)
        delay(1)
        telejump(dest)
    }

    private suspend fun ProtectedAccess.climbOption() {
        arriveDelay()
        startDialogue(dialogues) {
            val translate =
                choice2("Climb Up.", 1, "Climb Down.", -1, title = "Climb up or down the ladder?")
            val dest = player.coords.translateLevel(translate)
            anim(seqs.human_reachforladddertop)
            delay(2)
            telejump(dest)
        }
    }
}
