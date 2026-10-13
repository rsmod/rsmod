package org.runerealm.content.areas.city.lumbridge.npcs

import org.runerealm.api.player.dialogue.Dialogue
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.script.onOpNpc1
import org.runerealm.api.script.onOpNpc3
import org.runerealm.api.type.refs.interf.InterfaceReferences
import org.runerealm.api.type.refs.obj.ObjReferences
import org.runerealm.content.areas.city.lumbridge.configs.lumbridge_npcs
import org.runerealm.game.entity.Npc
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

/**
 * Perdu is a multinpc (`lost_property_merchant_standard`). Op triggers are published under the base
 * npc type's id, so handlers are registered on the base type rather than the visible variant
 * (`lost_property_merchant_model`).
 */
class Perdu : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(lumbridge_npcs.perdu) { perduDialogue(it.npc) }
        onOpNpc3(lumbridge_npcs.perdu) { openLostProperty() }
    }

    private suspend fun ProtectedAccess.perduDialogue(npc: Npc) = startDialogue(npc) { perdu() }

    // TODO(content): Owed experience claim dialogue.
    private suspend fun Dialogue.perdu() {
        chatNpc(
            neutral,
            "Hello. I've found some stuff that I think is yours. You can pay me with cash " +
                "you're carrying, or I can take payments from your bank. Would you like to buy " +
                "anything from me?",
        )
        val choice =
            choice4(
                "Why do you charge for returning lost items?",
                1,
                "Show me what you've found.",
                2,
                "About items that break when people kill me...",
                3,
                "No thanks.",
                4,
                title = "What would you like to say?",
            )
        when (choice) {
            1 -> whyCharge()
            2 -> showFound()
            3 -> breakableItems()
            4 -> chatPlayer(neutral, "No thanks.")
        }
    }

    private suspend fun Dialogue.whyCharge() {
        chatPlayer(quiz, "Why do you charge for returning lost items?")
        chatNpc(
            neutral,
            "You could get your stuff back elsewhere, sure. Now, maybe it'd be free, or maybe " +
                "you'd have to pay someone, but you'd definitely have to go on a journey to get " +
                "it back.",
        )
        chatNpc(happy, "So I'm earning my fee by saving you a trip. Do you want to have a look?")
        val choice =
            choice2(
                "Show me what you've found.",
                true,
                "No thanks.",
                false,
                title = "What would you like to say?",
            )
        if (choice) {
            showFound()
        } else {
            chatPlayer(neutral, "No thanks.")
        }
    }

    private suspend fun Dialogue.showFound() {
        chatPlayer(neutral, "Show me what you've found.")
        access.openLostProperty()
    }

    private suspend fun Dialogue.breakableItems() {
        chatPlayer(quiz, "About items that break when people kill me...")
        chatNpc(
            neutral,
            "Oh yes, some items do break specially when you're killed by your rivals. And if " +
                "you die in the deep Wilderness, they vanish altogether.",
        )
        val choice =
            choice2(
                "Can you fix them?",
                1,
                "Can you stop them vanishing in deep Wilderness?",
                2,
                title = "Select an Option",
            )
        if (choice == 1) {
            chatPlayer(quiz, "Can you fix them?")
            chatNpc(neutral, "For a fee, yes. Just show me the broken item.")
            return
        }
        chatPlayer(quiz, "Can you stop them vanishing in deep Wilderness?")
        chatNpc(
            neutral,
            "I can lock them to you so they won't vanish on death in deep Wilderness, yes. " +
                "I'll want some money for my services, though, and you'll need to bring me a " +
                "Trouver Parchment.",
        )
        if (PerduObjs.trouver_parchment in player.inv) {
            chatPlayer(happy, "I've got the parchment.")
            chatNpc(
                happy,
                "Not bad for a human! Okay, show me the item you want to protect, and I'll see " +
                    "what I can do.",
            )
            return
        }
        objbox(
            PerduObjs.trouver_parchment,
            "Perdu tells you about a merchant in Ferox Enclave who sells special scrolls " +
                "called Trouver Parchments.",
        )
        chatNpc(
            neutral,
            "You'll find her in Ferox Enclave, where people play a game called Last Man " +
                "Standing. Bring the parchment here, then show me the item you want to lock.",
        )
    }
}

/**
 * Opens the Lost Property shop. `lost_property_init` draws one entry for every bit set in its two
 * "lost item" bitmasks (indices into `enum_767`).
 *
 * TODO(content): Track which reward items the player has unlocked and lost, and handle "Buy". Until
 *   then the shop is always empty, which is what a player who has lost nothing sees.
 */
private fun ProtectedAccess.openLostProperty() {
    ifOpenMainModal(PerduInterfaces.lost_property)
    runClientScript(LOST_PROPERTY_INIT, 0, 0, -1)
}

private const val LOST_PROPERTY_INIT = 226

internal object PerduInterfaces : InterfaceReferences() {
    val lost_property = find("lost_property")
}

internal object PerduObjs : ObjReferences() {
    val trouver_parchment = find("trouver_parchment")
}
