package org.rsmod.content.areas.city.lumbridge.npcs

import jakarta.inject.Inject
import org.rsmod.api.dialogue.Dialogues
import org.rsmod.api.dialogue.startDialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onApNpc1
import org.rsmod.api.script.onOpNpc1
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.Player
import org.rsmod.map.CoordGrid
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class Bartender @Inject constructor(private val dialogues: Dialogues) : PluginScript() {
    override fun ScriptContext.startUp() {
        onApNpc1(LumbridgeNpcs.bartender) { apDialogue(it.npc) }
        onOpNpc1(LumbridgeNpcs.bartender) { startDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.apDialogue(npc: Npc) {
        val dest = npc.coords.translate(-2, 0)
        if (!player.isInBuilding() || player.x > dest.x) {
            apRange(-1)
            return
        } else if (player.coords == dest) {
            startDialogue(npc)
            return
        }
        delay(1)
        move(dest)
        player.facePathingEntitySquare(npc)
        startDialogue(npc)
    }

    private suspend fun ProtectedAccess.startDialogue(npc: Npc) =
        startDialogue(dialogues) {
            chatNpcSpecific(npc.type, happy, "Welcome to the Sheared Ram. What can I do for you?")
            val option =
                choice3(
                    "I'll have a beer please.",
                    1,
                    "Heard any rumours recently?",
                    2,
                    "Nothing, I'm fine.",
                    3,
                )
            when (option) {}
        }

    private fun Player.isInBuilding(): Boolean =
        isWithinArea(CoordGrid(0, 50, 50, 28, 36), CoordGrid(0, 50, 50, 33, 42)) ||
            isWithinArea(CoordGrid(0, 50, 50, 26, 39), CoordGrid(0, 50, 50, 27, 42))
}
