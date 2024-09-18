package org.rsmod.content.areas.city.lumbridge.npcs

import jakarta.inject.Inject
import org.rsmod.api.config.refs.objs
import org.rsmod.api.dialogue.Dialogue
import org.rsmod.api.dialogue.Dialogues
import org.rsmod.api.dialogue.startDialogue
import org.rsmod.api.invtx.invAddOrDrop
import org.rsmod.api.invtx.invTakeFee
import org.rsmod.api.player.output.spam
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.repo.obj.ObjRepository
import org.rsmod.api.script.onApNpc1
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.areas.city.lumbridge.configs.LumbridgeNpcs
import org.rsmod.game.entity.Npc
import org.rsmod.map.CoordGrid
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class Bartender
@Inject
constructor(private val dialogues: Dialogues, private val objRepo: ObjRepository) : PluginScript() {
    override fun ScriptContext.startUp() {
        onApNpc1(LumbridgeNpcs.bartender) { apDialogue(it.npc) }
        onOpNpc1(LumbridgeNpcs.bartender) { startDialogue(it.npc) }
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
        delay(1)
        move(dest)
        faceEntitySquare(npc)
        startDialogue(npc)
    }

    private suspend fun ProtectedAccess.startDialogue(npc: Npc) =
        startDialogue(dialogues, npc) {
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

    private suspend fun Dialogue.nothing() {
        chatPlayer(neutral, "Nothing, I'm fine.")
    }

    private fun ProtectedAccess.shouldPathToCounter(): Boolean =
        isWithinArea(CoordGrid(0, 50, 50, 28, 36), CoordGrid(0, 50, 50, 30, 42)) ||
            isWithinArea(CoordGrid(0, 50, 50, 26, 39), CoordGrid(0, 50, 50, 27, 42))
}
