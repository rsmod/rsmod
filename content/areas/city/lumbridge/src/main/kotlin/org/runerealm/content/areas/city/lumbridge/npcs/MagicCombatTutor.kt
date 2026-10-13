package org.runerealm.content.areas.city.lumbridge.npcs

import org.runerealm.api.config.refs.objs
import org.runerealm.api.config.refs.varps
import org.runerealm.api.player.dialogue.Dialogue
import org.runerealm.api.player.output.ClientScripts.toplevelSidebuttonSwitch
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.player.stat.baseMagicLvl
import org.runerealm.api.player.stat.baseRunecraftingLvl
import org.runerealm.api.script.onOpNpc1
import org.runerealm.api.script.onOpNpc3
import org.runerealm.api.type.refs.varbit.VarBitReferences
import org.runerealm.api.type.refs.varp.VarpReferences
import org.runerealm.content.areas.city.lumbridge.configs.lumbridge_npcs
import org.runerealm.game.entity.Npc
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

/**
 * Mikasi, the Magic combat tutor.
 *
 * @see <a href="https://oldschool.runescape.wiki/w/Transcript:Magic_combat_tutor">Transcript</a>
 */
class MagicCombatTutor : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(lumbridge_npcs.magic_tutor) { startDialogue(it.npc) }
        onOpNpc3(lumbridge_npcs.magic_tutor) { claim(it.npc) }
    }

    private suspend fun ProtectedAccess.startDialogue(npc: Npc) {
        startDialogue(npc) {
            chatNpc(
                happy,
                "Hello there adventurer, I am the Magic combat tutor. " +
                    "Would you like to learn about magic combat, or perhaps " +
                    "how to make runes?",
            )
            mainMenu()
        }
    }

    private suspend fun Dialogue.mainMenu() {
        val choice =
            choice4(
                "Tell me about magic combat please.",
                1,
                "How do I make runes?",
                2,
                "Let's discuss how my runes act when I pick them up.",
                3,
                "Goodbye.",
                4,
            )
        when (choice) {
            1 -> magicCombat()
            2 -> makeRunes()
            3 -> runePickup()
            4 -> goodbye()
        }
    }

    private suspend fun Dialogue.anythingElse() {
        chatNpc(happy, "Is there anything else you would like to know?")
        mainMenu()
    }

    private suspend fun Dialogue.magicCombat() {
        chatPlayer(quiz, "Tell me about magic combat please.")
        if (player.baseMagicLvl < 42) {
            beginnerMagic()
        } else {
            advancedMagic()
        }
        anythingElse()
    }

    private suspend fun Dialogue.beginnerMagic() {
        toplevelSidebuttonSwitch(player, SIDE_MAGIC)
        chatNpc(
            neutral,
            "This is your spell book, this contains all the spells you " +
                "can cast. To cast a spell, you will need to have the " +
                "required magic level and have the right amount of the " +
                "runes used to cast the spell.",
        )
        chatNpc(
            neutral,
            "To check the level and runes required to cast a spell, " +
                "hover your mouse cursor over the spell.",
        )
        chatNpc(
            neutral,
            "If you have the right amount of runes and the magic level " +
                "the spell will light up, meaning you will be able to cast " +
                "it.",
        )
        chatNpc(
            neutral,
            "When you have a combat spell available, click on it once, " +
                "then select your target. A good target would be a " +
                "monster that is below your combat level.",
        )
        chatNpc(
            neutral,
            "Try rats in the castle or, if you're feeling brave, the " +
                "goblins to the west have been causing a nuisance of " +
                "themselves.",
        )
        chatNpc(
            neutral,
            "Nemarti, the Ranged Combat tutor and I both give out " +
                "items every 30 minutes, however you must choose " +
                "whether you want runes or ranged equipment.",
        )
        chatNpc(
            neutral,
            "To claim runes, right-click on me and choose Claim, to " +
                "claim ranged equipment right-click on the Ranged " +
                "Combat tutor and select Claim.",
        )
        // TODO(content): Adventure paths eligibility line ("If you need some more advice, you
        //  should talk to Adventurer Jon about Adventure paths...") once adventure paths exist.
    }

    private suspend fun Dialogue.advancedMagic() {
        chatNpc(
            happy,
            "Of course ${player.displayName}! As a rule of thumb, if you " +
                "cast the highest spell of which you're capable, you'll get " +
                "the best experience possible.",
        )
        chatNpc(
            neutral,
            "Wearing metal armour and ranged armour can seriously " +
                "impair your magical abilities. Make sure you wear some " +
                "robes to maximise your capabilities.",
        )
        chatNpc(
            neutral,
            "Superheat Item and the Alchemy spells are good ways to " +
                "level magic if you are not interested in the combat " +
                "aspect of magic.",
        )
        chatNpc(
            neutral,
            "There's always the Magic Training Arena. You can find it " +
                "north of the Emir's Arena, north-east of Al Kharid. You " +
                "will be able to earn some special rewards there by " +
                "practicing your magic there.",
        )
        if (vars[MagicTutorVarps.magearena] >= MAGE_ARENA_COMPLETE) {
            chatNpc(
                happy,
                "I see you have already met Kolodion, you should keep " +
                    "practicing those spells. Maybe you will be able to use " +
                    "them outside the arena after practicing them enough " +
                    "times.",
            )
            return
        }
        chatNpc(
            neutral,
            "If you want a challenge, Kolodion, the master of battle " +
                "magic, has set up an arena deep in the Wilderness. There " +
                "you will have the chance to learn some extremely " +
                "powerful spells.",
        )
        if (vars[MagicTutorVarBits.deserttreasure] >= DESERT_TREASURE_COMPLETE) {
            chatNpc(
                happy,
                "I see you already have access to the ancient magicks. " +
                    "Well done, these will aid you greatly.",
            )
        } else {
            chatNpc(
                neutral,
                "You should investigate the treasure in the desert, you " +
                    "may find great benefit to your magic.",
            )
            chatNpc(
                worried,
                "Speak to the Archaeologist in the Bedabin Camp, south- " +
                    "west of the Shantay Pass. But be careful, this quest is " +
                    "not for the faint of heart and can be very dangerous.",
            )
        }
    }

    private suspend fun Dialogue.makeRunes() {
        chatPlayer(quiz, "How do I make runes?")
        chatNpc(
            neutral,
            "There are a couple of things you will need to make runes, " +
                "rune essence and a talisman to enter the temple ruins.",
        )
        if (vars[varps.runemysteries] >= RUNE_MYSTERIES_COMPLETE) {
            chatNpc(
                neutral,
                "To get rune essence you will need to gather them in the " +
                    "essence mine. You can get to the mine by talking to " +
                    "Aubury who owns the runes shop in south east Varrock.",
            )
        } else {
            chatNpc(
                neutral,
                "To get rune essence you will need to gather them " +
                    "somehow. You should talk to the Duke of Lumbridge, he " +
                    "may be able to help you with that. Alternatively, other " +
                    "players may sell you the essence.",
            )
        }
        if (player.baseRunecraftingLvl < 5) {
            chatNpc(
                neutral,
                "As you're fairly new to runecrafting you should start " +
                    "with air runes and mind runes.",
            )
        } else {
            chatNpc(
                happy,
                "I see you have some experience already in Runecrafting. " +
                    "Perhaps you should try crafting some runes which you " +
                    "can then use in magic.",
            )
            chatNpc(neutral, "Check the skill guide to see which runes you can craft.")
            toplevelSidebuttonSwitch(player, SIDE_SKILLS)
        }
        chatNpc(
            neutral,
            "You will need a talisman for the rune you would like to " +
                "create. You can right-click on it and select the Locate " +
                "option. This will tell you the rough location of the " +
                "altar.",
        )
        chatNpc(
            neutral,
            "When you find the ruined altar, use the talisman on it to " +
                "be transported to a temple where you can craft your " +
                "runes.",
        )
        chatNpc(
            neutral,
            "Clicking on the temple's altar will imbue your rune " +
                "essence with the altar's magical property.",
        )
        chatNpc(
            neutral,
            "If you want to save yourself an inventory space, you " +
                "could always try binding the talisman to a tiara.",
        )
        chatNpc(
            neutral,
            "To make one, take a tiara and talisman to the ruins and " +
                "use the tiara on the temple altar. This will bind the " +
                "talisman to your tiara.",
        )
        anythingElse()
    }

    private suspend fun Dialogue.runePickup() {
        chatPlayer(quiz, "Let's discuss how my runes act when I pick them up.")
        chatNpc(
            neutral,
            "When you pick up runes from the ground, if you're " +
                "carrying a rune pouch that contains matching runes, I " +
                "can make the runes you've picked up go straight into the " +
                "pouch, provided there's space.",
        )
        chatNpc(neutral, "Alternatively, you can let them go into your inventory like normal.")
        val toPouch =
            choice2(
                "Automatically send to rune pouch.",
                true,
                "Place them in my inventory.",
                false,
                title = "How do you want to handle picking up runes?",
            )
        if (toPouch) {
            vars[MagicTutorVarBits.take_runes_toggle] = 1
            chatNpc(
                happy,
                "Certainly! Runes will go straight to your rune pouch " +
                    "when you pick them up, if it contains matching runes.",
            )
        } else {
            vars[MagicTutorVarBits.take_runes_toggle] = 0
            chatNpc(
                happy,
                "Certainly! Runes will go to your inventory when you pick " +
                    "them up, rather than going into a rune pouch.",
            )
        }
        mesbox(
            "You can also control this option via the Settings menu, " +
                "rather than having to return to this Tutor in future."
        )
        chatNpc(happy, "Is there anything else I can do for you?")
        mainMenu()
    }

    private suspend fun Dialogue.goodbye() {
        chatPlayer(neutral, "Goodbye.")
    }

    private suspend fun ProtectedAccess.claim(npc: Npc) {
        startDialogue(npc) { claimRunes() }
    }

    private suspend fun Dialogue.claimRunes() {
        if (TutorClaims.onCooldown(player)) {
            with(TutorClaims) { cooldownReply("Ranged Combat tutor", "runes", "make your own") }
            return
        }
        val mindInBank = objs.mind_rune in access.bank
        val airInBank = objs.air_rune in access.bank

        var mindGiven = false
        when {
            mindInBank -> chatNpc(neutral, "You have some mind runes in your bank.")
            objs.mind_rune in player.inv -> chatNpc(neutral, "You already have some mind runes.")
            !player.inv.hasFreeSpace() -> noSpace("mind")
            else -> {
                access.invAdd(player.inv, objs.mind_rune, count = RUNE_COUNT)
                mindGiven = true
            }
        }

        val airInInv = objs.air_rune in player.inv
        var airGiven = false
        when {
            airInBank -> chatNpc(neutral, "You have some air runes in your bank.")
            airInInv -> chatNpc(neutral, "You already have some air runes.")
            !player.inv.hasFreeSpace() -> noSpace("air")
            else -> {
                access.invAdd(player.inv, objs.air_rune, count = RUNE_COUNT)
                airGiven = true
            }
        }

        if (mindGiven || airGiven) {
            TutorClaims.markClaimed(player)
        }
        if (mindGiven) {
            mesbox("Mikasi gives you 30 mind runes.")
        }
        if (airGiven) {
            mesbox("Mikasi gives you 30 air runes.")
        }
        if (airInBank || (mindInBank && airInInv)) {
            objbox(
                objs.bank_icon,
                "You have some runes in your bank. Climb the stairs in " +
                    "Lumbridge Castle until you see this icon on your " +
                    "minimap. There you will find a bank.",
            )
        }
    }

    private suspend fun Dialogue.noSpace(rune: String) {
        chatNpc(
            sad,
            "If you had enough space in your inventory I'd give you " +
                "some $rune runes, come back when you do.",
        )
    }

    private companion object {
        const val RUNE_COUNT = 30

        const val SIDE_SKILLS = 1
        const val SIDE_MAGIC = 6

        // Completion values as used by the quest journal / RuneLite quest states.
        const val RUNE_MYSTERIES_COMPLETE = 6
        const val MAGE_ARENA_COMPLETE = 8
        const val DESERT_TREASURE_COMPLETE = 15
    }
}

internal object MagicTutorVarps : VarpReferences() {
    val magearena = find("magearena")
}

internal object MagicTutorVarBits : VarBitReferences() {
    val deserttreasure = find("deserttreasure")
    val take_runes_toggle = find("take_runes_toggle")
}
