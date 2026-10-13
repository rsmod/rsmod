package org.runerealm.content.areas.city.lumbridge.npcs

import jakarta.inject.Inject
import org.runerealm.api.invtx.invAddOrDrop
import org.runerealm.api.invtx.invTakeFee
import org.runerealm.api.player.dialogue.Dialogue
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.player.vars.boolVarBit
import org.runerealm.api.repo.obj.ObjRepository
import org.runerealm.api.script.onOpNpc1
import org.runerealm.api.type.refs.obj.ObjReferences
import org.runerealm.api.type.refs.varbit.VarBitReferences
import org.runerealm.content.areas.city.lumbridge.configs.lumbridge_npcs
import org.runerealm.game.entity.Npc
import org.runerealm.game.entity.Player
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

/** Dialogue follows https://oldschool.runescape.wiki/w/Transcript:Candle_seller */
class CandleSeller @Inject constructor(private val objRepo: ObjRepository) : PluginScript() {
    private var Player.heardLanternPitch by boolVarBit(candle_seller_varbits.conversation)

    override fun ScriptContext.startup() {
        onOpNpc1(lumbridge_npcs.candle_seller) { startDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.startDialogue(npc: Npc) =
        startDialogue(npc) { candleSellerDialogue() }

    private suspend fun Dialogue.candleSellerDialogue() {
        chatNpc(neutral, "Do you want a lit candle for 1000 gold?")
        val option =
            if (player.heardLanternPitch) {
                choice4(
                    "Yes please.",
                    1,
                    "One thousand gold?!",
                    2,
                    "No thanks, I'd rather curse the darkness.",
                    3,
                    "How do you make lanterns?",
                    4,
                )
            } else {
                choice3(
                    "Yes please.",
                    1,
                    "One thousand gold?!",
                    2,
                    "No thanks, I'd rather curse the darkness.",
                    3,
                )
            }
        when (option) {
            1 -> yesPlease()
            2 -> oneThousandGold()
            3 -> curseTheDarkness()
            4 -> howToMakeLanterns()
        }
    }

    private suspend fun Dialogue.yesPlease() {
        chatPlayer(happy, "Yes please.")
        buyCandle()
    }

    private suspend fun Dialogue.youWin() {
        chatPlayer(sad, "All right, you win, I'll buy a candle.")
        buyCandle()
    }

    private suspend fun Dialogue.buyCandle() {
        if (!player.invTakeFee(CANDLE_PRICE)) {
            chatPlayer(sad, "But I don't have that kind of money on me.")
            chatNpc(neutral, "Well then, no candle for you!")
            return
        }
        player.invAddOrDrop(objRepo, candle_seller_objs.lit_candle)
        chatNpc(happy, "Here you go then.")
        chatNpc(
            neutral,
            "I should warn you, though, it can be dangerous to take a naked flame down " +
                "there. You'd be better off making a lantern.",
        )
        val option =
            choice3(
                "What's so dangerous about a naked flame?",
                1,
                "How do you make lanterns?",
                2,
                "Thanks, bye.",
                3,
            )
        when (option) {
            1 -> nakedFlame()
            2 -> howToMakeLanterns()
            3 -> thanksBye()
        }
    }

    private suspend fun Dialogue.nakedFlame() {
        chatPlayer(quiz, "What's so dangerous about a naked flame?")
        chatNpc(laugh, "Heh heh... You'll find out.")
    }

    private suspend fun Dialogue.thanksBye() {
        chatPlayer(happy, "Thanks, bye.")
    }

    private suspend fun Dialogue.oneThousandGold() {
        chatPlayer(shocked, "One thousand gold?!")
        chatNpc(
            neutral,
            "Look, you're not going to be able to survive down that hole without a light source.",
        )
        chatNpc(
            neutral,
            "So you could go off to the candle shop to buy one more cheaply. You could even " +
                "make your own lantern, which is a lot better.",
        )
        chatNpc(
            laugh,
            "But I bet you want to find out what's down there right now, don't you? And you " +
                "can pay me 1000 gold for the privilege!",
        )
        // Per the wiki transcript, varbit 284 switches to 1 at this point, which adds the
        // "How do you make lanterns?" option to the initial menu.
        player.heardLanternPitch = true
        val option =
            choice3(
                "All right, you win, I'll buy a candle.",
                1,
                "No way.",
                2,
                "How do you make lanterns?",
                3,
            )
        when (option) {
            1 -> youWin()
            2 -> noWay()
            3 -> howToMakeLanterns()
        }
    }

    private suspend fun Dialogue.noWay() {
        chatPlayer(angry, "No way.")
    }

    private suspend fun Dialogue.howToMakeLanterns() {
        chatPlayer(quiz, "How do you make lanterns?")
        chatNpc(neutral, "Out of glass. The more advanced lanterns have a metal component as well.")
        chatNpc(
            neutral,
            "Firstly you can make a simple candle lantern out of glass. It's just like a " +
                "candle, but the flame isn't exposed, so it's safer.",
        )
        chatNpc(
            neutral,
            "Then you can make an oil lamp, which is brighter but has an exposed flame. But " +
                "if you make an iron frame for it you can turn it into an oil lantern.",
        )
        chatNpc(
            neutral,
            "Finally there's the bullseye lantern. You'll need to make a frame out of steel " +
                "and add a glass lens.",
        )
        chatNpc(
            neutral,
            "Once you've made your lamp or lantern, you'll need to make lamp oil for it. The " +
                "chemist near Rimmington has a machine for that.",
        )
        chatNpc(
            neutral,
            "For any light source, you'll need a tinderbox to light it. Keep your tinderbox " +
                "handy in case it goes out!",
        )
        chatNpc(
            laugh,
            "But if all that's too complicated, you can buy a candle right here for 1000 gold!",
        )
        val option =
            choice2(
                "All right, you win, I'll buy a candle.",
                1,
                "No thanks, I'd rather curse the darkness.",
                2,
            )
        when (option) {
            1 -> youWin()
            2 -> curseTheDarkness()
        }
    }

    private suspend fun Dialogue.curseTheDarkness() {
        chatPlayer(neutral, "No thanks, I'd rather curse the darkness.")
    }

    private companion object {
        const val CANDLE_PRICE = 1000
    }
}

internal object CandleSellerObjs : ObjReferences() {
    val lit_candle = find("lit_candle")
}

internal object CandleSellerVarBits : VarBitReferences() {
    val conversation = find("swamp_candle_guy_conversation")
}

private typealias candle_seller_objs = CandleSellerObjs

private typealias candle_seller_varbits = CandleSellerVarBits
