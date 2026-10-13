package org.runerealm.content.areas.city.lumbridge.npcs

import org.runerealm.api.player.dialogue.Dialogue
import org.runerealm.api.player.output.ClientScripts.toplevelSidebuttonSwitch
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.player.stat.baseRangedLvl
import org.runerealm.api.script.onOpNpc1
import org.runerealm.api.script.onOpNpc3
import org.runerealm.api.script.onOpNpcU
import org.runerealm.api.type.refs.obj.ObjReferences
import org.runerealm.api.type.refs.varbit.VarBitReferences
import org.runerealm.content.areas.city.lumbridge.configs.lumbridge_npcs
import org.runerealm.game.entity.Npc
import org.runerealm.game.type.obj.ObjType
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

/**
 * Nemarti, the Ranged combat tutor.
 *
 * @see <a href="https://oldschool.runescape.wiki/w/Transcript:Ranged_combat_tutor">Transcript</a>
 */
class RangedCombatTutor : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(lumbridge_npcs.ranged_tutor) { startDialogue(it.npc) }
        onOpNpc3(lumbridge_npcs.ranged_tutor) { claim(it.npc) }
        onOpNpcU(lumbridge_npcs.ranged_tutor, RangedTutorObjs.training_bow) {
            returnItem(it.npc, it.invSlot, RangedTutorObjs.training_bow, "Training bow", "bow")
        }
        onOpNpcU(lumbridge_npcs.ranged_tutor, RangedTutorObjs.training_arrows) {
            returnItem(
                it.npc,
                it.invSlot,
                RangedTutorObjs.training_arrows,
                "Training arrows",
                "arrows",
            )
        }
        onOpNpcU(lumbridge_npcs.ranged_tutor) { noUse(it.npc) }
    }

    private suspend fun ProtectedAccess.startDialogue(npc: Npc) {
        startDialogue(npc) {
            chatNpc(
                happy,
                "Hey there adventurer, I am the Ranged combat tutor. Is " +
                    "there anything you would like to know?",
            )
            mainMenu()
        }
    }

    private suspend fun Dialogue.mainMenu() {
        val choice =
            choice4(
                "How can I train my Ranged?",
                1,
                "How do I create a bow and arrows?",
                2,
                "Can you toggle my ammo to equip when I pick it up please?",
                3,
                "Goodbye.",
                4,
            )
        when (choice) {
            1 -> trainRanged()
            2 -> fletching()
            3 -> ammoToggle()
            4 -> goodbye()
        }
    }

    private suspend fun Dialogue.anythingElse() {
        chatNpc(happy, "Is there anything else you want to know?")
        mainMenu()
    }

    private suspend fun Dialogue.trainRanged() {
        chatPlayer(quiz, "How can I train my Ranged?")
        val level = player.baseRangedLvl
        when {
            level < 29 -> beginnerAdvice()
            level < 44 -> intermediateAdvice()
            else -> advancedAdvice(level)
        }
        lowesArcheryEmporium()
    }

    private suspend fun Dialogue.beginnerAdvice() {
        chatNpc(
            neutral,
            "To start with you'll need a bow and arrows, you were " +
                "given a Shortbow and some arrows when you arrived " +
                "here from Tutorial island.",
        )
        chatNpc(neutral, "Alternatively, you can claim a training bow and some arrows from me.")
        chatNpc(
            neutral,
            "Mikasi, the Magic Combat tutor and I both give out items " +
                "every 30 minutes, however you must choose whether you " +
                "want runes or ranged equipment.",
        )
        chatNpc(
            neutral,
            "To claim the Training bow and arrows, right-click on me " +
                "and choose Claim, to claim runes right-click on the Magic " +
                "Combat tutor and select Claim.",
        )
        toplevelSidebuttonSwitch(player, SIDE_SKILLS)
        chatNpc(
            neutral,
            "Not all bows can use every type of arrow, most bows " +
                "have a limit. You can find out your bows limit by " +
                "checking the Ranged skill guide.",
        )
        chatNpc(
            neutral,
            "If you do decide to use the Training bow, you will only be " +
                "able to use the Training arrows with it. Remember to " +
                "pick up your arrows, re-use them and come back when " +
                "you need more.",
        )
        chatNpc(
            neutral,
            "Once you have your bow and arrows, equip them by " +
                "selecting their Wield option in your inventory.",
        )
        toplevelSidebuttonSwitch(player, SIDE_COMBAT)
        chatNpc(
            neutral,
            "You can change the way you attack by going to the " +
                "combat options tab. There are three attack styles for " +
                "bows. Those styles are Accurate, Rapid and Longrange.",
        )
        chatNpc(
            neutral,
            "Accurate increases your bows attack accuracy. Rapid will " +
                "increase your attack speed with the bow. Longrange will " +
                "let you attack your enemies from a greater distance.",
        )
        // TODO(content): Adventure paths eligibility line ("If you need some more advice, you
        //  should talk to Adventurer Jon about Adventure paths...") once adventure paths exist.
    }

    private suspend fun Dialogue.intermediateAdvice() {
        chatNpc(
            neutral,
            "Use the best bow and arrows you can get your hands on " +
                "and practically use, for your level. This will ensure " +
                "maximum damage.",
        )
        chatNpc(
            neutral,
            "Choose carefully where you shoot from. If you can, use " +
                "fences and other things to help protect you whilst you " +
                "fight.",
        )
        chatNpc(
            neutral,
            "You may also find that the better the ranged armour you " +
                "use, the more accurate you will be with your bow.",
        )
    }

    private suspend fun Dialogue.advancedAdvice(level: Int) {
        if (level >= 99) {
            chatNpc(
                laugh,
                "My word, I think you should be the one giving me advice " +
                    "instead, but I will do my best.",
            )
        }
        chatNpc(
            neutral,
            "You should know the basics of ranged combat by now, but " +
                "if you are looking for somewhere to test your skills and " +
                "make some money, you should try hunting Moss giants in " +
                "Varrock sewers.",
        )
        chatNpc(
            neutral,
            "If you are lucky you might find a Mossy key, with this key " +
                "you will be able to enter Bryophyta's lair. Make sure to " +
                "bring plenty of food and good armour and weapons when " +
                "you fight her as you will need it.",
        )
        chatNpc(
            happy,
            "If your lucky you might snag some goodies that you can sell for a decent profit.",
        )
        chatNpc(
            neutral,
            "If you are looking to do something different, visit the " +
                "ranging guild over near Hemenster, there you can " +
                "practice your archery and earn tickets for it at the same " +
                "time.",
        )
        chatNpc(
            neutral,
            "You can then hand those tickets in for a variety of goods " +
                "you can use to train your Ranged skill.",
        )
    }

    private suspend fun Dialogue.lowesArcheryEmporium() {
        chatNpc(
            neutral,
            "If you are ever in the market for a new bow or some " +
                "arrows, you should head on over to Lowe's Archery " +
                "Emporium in Varrock. I can show you where the shop is " +
                "if you like.",
        )
        val choice = choice2("Yes please.", 1, "No thanks.", 2)
        if (choice == 1) {
            chatPlayer(happy, "Yes please.")
            // TODO(content): Open the world map and pan to Lowe's Archery Emporium.
            return
        }
        chatPlayer(neutral, "No thanks")
        anythingElse()
    }

    private suspend fun Dialogue.fletching() {
        chatNpc(
            happy,
            "Ahh the art of fletching. Fletching is used to create your own bow and arrows.",
        )
        chatNpc(
            neutral,
            "It's quite simple really. You'll need an axe to cut some " +
                "logs from trees and a knife. Knives can be found in and " +
                "around the Lumbridge castle and in the Varrock General " +
                "store upstairs.",
        )
        chatNpc(
            neutral,
            "Use your knife on the logs. This will bring up a menu " +
                "listing items you can fletch.",
        )
        chatNpc(
            neutral,
            "For arrows you will need to smith some arrow heads and " +
                "kill some chickens for feathers.",
        )
        chatNpc(
            neutral,
            "Add the feathers to your Arrow shafts to make Headless " +
                "arrows, then use your chosen arrow heads on the " +
                "Headless arrows to make your arrows.",
        )
        chatNpc(
            neutral,
            "Now for making bows. When accessing the fletching " +
                "menu, instead of choosing Arrows shafts, you can make " +
                "an unstrung bow instead.",
        )
        chatNpc(neutral, "To complete the bow you will need to get your hands on a Bow string.")
        chatNpc(
            neutral,
            "First you will need to get some flax from a flax field. " +
                "There's one south of Seers' Village. Gather flax, then " +
                "spin it on a spinning wheel, there's one in Seers' Village " +
                "too.",
        )
        chatNpc(
            happy,
            "This makes bow strings which you can then use on the " +
                "unstrung bows to make a working bow!",
        )
        chatPlayer(happy, "Brilliant. If I forget anything I'll come talk to you again.")
        anythingElse()
    }

    private suspend fun Dialogue.ammoToggle() {
        chatNpc(
            happy,
            "Certainly. If you pick up ammo that is the same ammo " +
                "you are currently using, I can make that ammo " +
                "automatically equip for you, providing you have space " +
                "of course!",
        )
        chatNpc(neutral, "Or you can leave it to going into your inventory like normal.")
        val autoEquip =
            choice2(
                "Automatically equip it.",
                true,
                "Place it in my inventory.",
                false,
                title = "How do you want to handle picking up ammo?",
            )
        if (autoEquip) {
            vars[RangedTutorVarBits.take_ammo_toggle] = 1
            chatNpc(
                happy,
                "There you go! Your ammo will now automatically be " +
                    "equipped when you pick it up.",
            )
        } else {
            vars[RangedTutorVarBits.take_ammo_toggle] = 0
            chatNpc(
                happy,
                "There you go! Your ammo will now go to your inventory when you pick it up.",
            )
        }
        anythingElse()
    }

    private suspend fun Dialogue.goodbye() {
        chatPlayer(neutral, "Goodbye.")
    }

    private suspend fun ProtectedAccess.claim(npc: Npc) {
        startDialogue(npc) { claimTrainingEquipment() }
    }

    private suspend fun Dialogue.claimTrainingEquipment() {
        if (TutorClaims.onCooldown(player)) {
            with(TutorClaims) { cooldownReply("Magic tutor", "arrows", "buy some") }
            return
        }
        val bow = RangedTutorObjs.training_bow
        val arrows = RangedTutorObjs.training_arrows
        val hasBow = owns(bow)
        val hasArrows = owns(arrows)
        val freeSpace = player.inv.freeSpace()
        when {
            hasBow && hasArrows -> {
                chatNpc(neutral, "You already have a training bow.")
                chatNpc(neutral, "You already have some training arrows.")
            }
            hasArrows -> {
                if (freeSpace < 1) {
                    chatNpc(
                        sad,
                        "If you had any room in your pack I'd give you a " +
                            "training bow. That's a shame. Come back when you do.",
                    )
                    return
                }
                chatNpc(neutral, "You already have some training arrows.")
                access.invAdd(player.inv, bow)
                TutorClaims.markClaimed(player)
                objbox(
                    bow,
                    "Nemarti gives you a Training shortbow. It can only be " +
                        "used with Training arrows.",
                )
            }
            hasBow -> {
                if (freeSpace < 1) {
                    chatNpc(
                        sad,
                        "If you had enough space in your inventory I'd give " +
                            "you some training arrows, come back when you do.",
                    )
                    return
                }
                chatNpc(neutral, "You already have a training bow.")
                access.invAdd(player.inv, arrows, count = ARROW_COUNT)
                TutorClaims.markClaimed(player)
                objbox(
                    arrows,
                    "Nemarti gives you 25 training arrows. They can only be " +
                        "used with the Training shortbow.",
                )
            }
            else -> {
                if (freeSpace < 2) {
                    chatNpc(
                        sad,
                        "If you had any room in your pack I'd give you a " +
                            "training bow. That's a shame. Come back when you " +
                            "do. If you had enough space in your inventory I'd " +
                            "give you some training arrows, come back when you " +
                            "do.",
                    )
                    return
                }
                access.invAdd(player.inv, bow)
                access.invAdd(player.inv, arrows, count = ARROW_COUNT)
                TutorClaims.markClaimed(player)
                doubleobjbox(
                    bow,
                    arrows,
                    "Nemarti gives you a Training shortbow and 25 arrows. " +
                        "They can only be used together.",
                )
            }
        }
    }

    private fun Dialogue.owns(type: ObjType): Boolean =
        type in player.inv || type in player.worn || type in access.bank

    private suspend fun ProtectedAccess.returnItem(
        npc: Npc,
        invSlot: Int,
        type: ObjType,
        name: String,
        lowercase: String,
    ) {
        startDialogue(npc) {
            chatPlayer(quiz, "I no longer need my $name, would you like it back?")
            chatNpc(
                happy,
                "Sure thing, I can give this to another adventurer who is in need of training.",
            )
            val count = player.inv[invSlot]?.count ?: return@startDialogue
            access.invDel(player.inv, type, count = count, slot = invSlot)
            // Typo ("you") is on purpose; it matches the game.
            objbox(type, "You give back you training $lowercase to the Ranged combat tutor.")
        }
    }

    private suspend fun ProtectedAccess.noUse(npc: Npc) {
        startDialogue(npc) { chatNpc(neutral, "I have no use for that, sorry.") }
    }

    private companion object {
        const val ARROW_COUNT = 25

        const val SIDE_COMBAT = 0
        const val SIDE_SKILLS = 1
    }
}

internal object RangedTutorObjs : ObjReferences() {
    val training_bow = find("aide_shortbow")
    val training_arrows = find("aide_arrow")
}

internal object RangedTutorVarBits : VarBitReferences() {
    val take_ammo_toggle = find("take_ammo_toggle")
}
