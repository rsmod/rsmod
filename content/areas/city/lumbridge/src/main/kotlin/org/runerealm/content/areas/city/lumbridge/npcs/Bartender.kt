package org.runerealm.content.areas.city.lumbridge.npcs

import jakarta.inject.Inject
import org.runerealm.api.config.refs.objs
import org.runerealm.api.invtx.invAddOrDrop
import org.runerealm.api.invtx.invTakeFee
import org.runerealm.api.player.dialogue.Dialogue
import org.runerealm.api.player.output.spam
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.repo.obj.ObjRepository
import org.runerealm.api.script.onApNpc1
import org.runerealm.api.script.onOpNpc1
import org.runerealm.content.areas.city.lumbridge.configs.lumbridge_npcs
import org.runerealm.game.entity.Npc
import org.runerealm.map.CoordGrid
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

class Bartender @Inject constructor(private val objRepo: ObjRepository) : PluginScript() {
    override fun ScriptContext.startup() {
        onApNpc1(lumbridge_npcs.bartender) { apDialogue(it.npc) }
        onOpNpc1(lumbridge_npcs.bartender) { startDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.apDialogue(npc: Npc) {
        val dest = npc.coords.translate(-2, 0)
        if (!shouldPathToCounter()) {
            apRange(-1)
            return
        } else if (coords == dest) {
            startDialogue(npc)
            return
        }
        walk(coords)
        delay(1)
        playerMove(dest)
        startDialogue(npc)
    }

    private suspend fun ProtectedAccess.startDialogue(npc: Npc) =
        startDialogue(npc) {
            chatNpcNoTurn(happy, "Welcome to the Sheared Ram. What can I do for you?")
            val option =
                choice3(
                    "I'll have a beer please.",
                    1,
                    "Heard any rumours recently?",
                    2,
                    "Nothing, I'm fine.",
                    3,
                )
            when (option) {
                1 -> requestBeer()
                2 -> requestRumour()
                3 -> nothing()
            }
        }

    private suspend fun Dialogue.requestBeer() {
        chatPlayer(happy, "I'll have a beer please.")
        chatNpcNoTurn(happy, "That'll be two coins please.")
        if (!player.invTakeFee(fee = 2)) {
            chatPlayer(sad, "Oh dear, I don't seem to have enough money.")
        } else {
            player.spam("You buy a pint of beer.")
            player.invAddOrDrop(objRepo, objs.beer)
        }
    }

    private suspend fun Dialogue.requestRumour() {
        chatPlayer(quiz, "Heard any rumours recently?")
        chatNpcNoTurn(
            neutral,
            "One of the patrons here is looking for treasure " +
                "apparently. A chap by the name of Veos.",
        )
    }

    // The conversation ends without the player repeating the option. (OSRS Wiki transcript)
    private fun nothing() {}

    private fun ProtectedAccess.shouldPathToCounter(): Boolean =
        isWithinArea(CoordGrid(0, 50, 50, 28, 36), CoordGrid(0, 50, 50, 30, 42)) ||
            isWithinArea(CoordGrid(0, 50, 50, 26, 39), CoordGrid(0, 50, 50, 27, 42))
}
