package org.runerealm.content.areas.city.lumbridge.npcs

import org.runerealm.api.config.refs.objs
import org.runerealm.api.player.dialogue.Dialogue
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.player.stat.baseMiningLvl
import org.runerealm.api.script.onOpNpc1
import org.runerealm.api.type.refs.obj.ObjReferences
import org.runerealm.content.areas.city.lumbridge.configs.lumbridge_npcs
import org.runerealm.game.entity.Npc
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

/**
 * Monlum, the Mining tutor.
 *
 * @see <a href="https://oldschool.runescape.wiki/w/Transcript:Mining_tutor">Transcript</a>
 */
class MiningTutor : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(lumbridge_npcs.mining_tutor) { startDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.startDialogue(npc: Npc) {
        startDialogue(npc) { mainMenu() }
    }

    private suspend fun Dialogue.mainMenu() {
        val level = player.baseMiningLvl
        val levelOption =
            when {
                level < 25 -> "Can you teach me the basics of mining please?"
                level < 39 -> "I already know about the basics of mining, got any tips?"
                else -> "Any advice for an advanced miner?"
            }
        val choice =
            choice4(
                levelOption,
                1,
                "Are there any mining related quests?",
                2,
                "Tell me about different rocks and picks.",
                3,
                "Goodbye.",
                4,
            )
        when (choice) {
            1 ->
                when {
                    level < 25 -> basics()
                    level < 39 -> tips()
                    else -> advancedAdvice()
                }
            2 -> quests()
            3 -> rocksAndPicks()
            4 -> goodbye()
        }
    }

    private suspend fun Dialogue.basics() {
        chatPlayer(quiz, "Can you teach me the basics of mining please?")
        objbox(
            MiningTutorObjs.mining_site_icon,
            "Look for this icon on your minimap to find mining rocks.",
        )
        givePickaxeIfRequired()
        chatNpc(
            neutral,
            "You can tell the ore you'll get from a rock by looking at " +
                "it. The colour of the rock helps, too.",
        )
        chatNpc(
            neutral,
            "To mine, simply click on the rock to mine it, while you've got a pickaxe with you.",
        )
        chatNpc(
            neutral,
            "When you have a full inventory, take it to the bank. " +
                "There's a bank on the roof of the castle in Lumbridge.",
        )
        objbox(
            objs.bank_icon,
            "Look for this symbol on your minimap after climbing the " +
                "stairs of the Lumbridge Castle to the top.",
        )
        mainMenu()
    }

    private suspend fun Dialogue.tips() {
        chatPlayer(quiz, "I already know about the basics of mining, got any tips?")
        givePickaxeIfRequired()
        chatNpc(
            neutral,
            "You can get different rocks in different places " +
                "throughout the land, explore and find them.",
        )
        chatNpc(
            neutral,
            "You could start by traveling to the Al Kharid mine. Follow " +
                "the path north and pay the 10 coins toll to go through " +
                "the gate. Head north after passing through the gate and " +
                "you will find the mine.",
        )
        chatNpc(worried, "Be careful though, the scorpions in the mine will attack you.")
        chatNpc(happy, "I could show you where Al Kharid mine is on your map if you like?")
        val choice = choice2("Yes please.", 1, "No thanks.", 2)
        if (choice == 1) {
            chatPlayer(happy, "Yes please.")
            // TODO(content): Open the world map and pan to the Al Kharid mine.
            return
        }
        chatPlayer(neutral, "No thanks.")
        mainMenu()
    }

    private suspend fun Dialogue.advancedAdvice() {
        chatPlayer(quiz, "Any advice for an advanced miner?")
        givePickaxeIfRequired()
        chatNpc(
            neutral,
            "Investigate the mining guild, the entrance to this is in the " +
                "east of Falador down some steps. It could be most useful " +
                "to you!",
        )
        chatNpc(
            neutral,
            "You might also like to check out the coal mine west of " +
                "Seers' village. This will enable you to use the coal " +
                "trucks.",
        )
        chatPlayer(happy, "Thank you, I'll remember that.")
        chatNpc(happy, "I could show you where either of the places are on your map if you like?")
        val choice =
            choice3(
                "Can you show me where the mining guild is?",
                1,
                "Can you show me where the coal mine is?",
                2,
                "No thanks.",
                3,
            )
        when (choice) {
            1 -> {
                chatPlayer(quiz, "Can you show me where the mining guild is?")
                // TODO(content): Open the world map and pan to the Mining Guild.
            }
            2 -> {
                chatPlayer(quiz, "Can you show me where the coal mine is?")
                // TODO(content): Open the world map and pan to the coal truck mining area.
            }
            3 -> {
                chatPlayer(neutral, "No thanks.")
                mainMenu()
            }
        }
    }

    private suspend fun Dialogue.givePickaxeIfRequired() {
        if (hasPickaxe()) {
            chatNpc(happy, "As you already have a pick, you can mine the rocks around me.")
            return
        }
        val add = access.invAdd(player.inv, objs.bronze_pickaxe)
        if (add.success) {
            chatNpc(
                happy,
                "As you're already here, have a pick so that you can mine the rocks around me.",
            )
        } else {
            chatNpc(
                sad,
                "I'd give you a pick to mine the rocks around me, but you " +
                    "don't have room in your inventory.",
            )
        }
    }

    private fun Dialogue.hasPickaxe(): Boolean =
        PICKAXES.any { it in player.inv || it in player.worn }

    private suspend fun Dialogue.quests() {
        chatNpc(
            happy,
            "Oh yes, if you haven't already, speak to Doric who can be " +
                "found around the anvils north of Falador. I'm sure he " +
                "can help you out.",
        )
        chatNpc(
            neutral,
            "You could investigate the Dig Site east of Varrock, I'm " +
                "sure you'll find something rewarding there.",
        )
        mainMenu()
    }

    private suspend fun Dialogue.rocksAndPicks() {
        chatPlayer(quiz, "Tell me about different rocks and picks.")
        chatNpc(happy, "Ok then.. here goes....")
        rocksAndPicksMenu()
    }

    private suspend fun Dialogue.rocksAndPicksMenu() {
        val choice = choice3("Rocks and Ores.", 1, "Pickaxes.", 2, "Teach me about mining.", 3)
        when (choice) {
            1 -> rocksMenu()
            2 -> pickaxesMenu()
            3 -> mainMenu()
        }
    }

    private suspend fun Dialogue.rocksMenu() {
        val choice =
            choice5(
                "Clay and Rune Essence.",
                1,
                "Copper and Tin.",
                2,
                "Iron and Coal.",
                3,
                "Teach me about mining.",
                4,
                "More...",
                5,
            )
        when (choice) {
            1 -> clayAndEssence()
            2 -> copperAndTin()
            3 -> ironAndCoal()
            4 -> mainMenu()
            5 -> moreRocksMenu()
        }
    }

    private suspend fun Dialogue.moreRocksMenu() {
        val choice =
            choice4(
                "Silver and Gold.",
                1,
                "Mithril, Adamantite and Runite.",
                2,
                "Teach me about mining.",
                3,
                "Back...",
                4,
            )
        when (choice) {
            1 -> silverAndGold()
            2 -> mithrilAdamantiteAndRunite()
            3 -> mainMenu()
            4 -> rocksMenu()
        }
    }

    private suspend fun Dialogue.afterRockMenu() {
        val choice = choice2("Teach me about mining.", 1, "Teach me about different rocks.", 2)
        when (choice) {
            1 -> mainMenu()
            2 -> rocksMenu()
        }
    }

    private suspend fun Dialogue.clayAndEssence() {
        objbox(
            MiningTutorObjs.clay,
            "Clay can be mined in many places. Try south west of " +
                "Varrock or the crafting guild. See the crafting tutor " +
                "upstairs in Lumbridge Castle for more information on " +
                "what you can make with clay.",
        )
        objbox(
            MiningTutorObjs.rune_essence,
            "Rune Essence can only be mined at secret places... I don't " +
                "know where these are but you can speak to the Duke of " +
                "Lumbridge to find out more.",
        )
        chatNpc(
            neutral,
            "You can make all sorts of things from clay, if you have the " +
                "crafting skill. Rune Essence is used for magic to cast " +
                "spells.",
        )
        afterRockMenu()
    }

    private suspend fun Dialogue.copperAndTin() {
        objbox(
            MiningTutorObjs.copper_ore,
            "Rocks which yield copper ore can be found around here, " +
                "and other places such as Al Kharid, Rimmington or " +
                "Varrock mines. They're quite common.",
        )
        objbox(
            MiningTutorObjs.tin_ore,
            "Tin ore is much the same as copper. It can be found " +
                "around this area as well as Varrock, Al Kharid and the " +
                "Barbarian Village.",
        )
        afterRockMenu()
    }

    private suspend fun Dialogue.ironAndCoal() {
        objbox(
            MiningTutorObjs.iron_ore,
            "Iron can be mined around Rimmington, Al Kharid and " +
                "Varrock. It's tricky to smelt though - see the boy at the " +
                "furnace in Lumbridge for tips.",
        )
        objbox(objs.furnace_icon, "Look for this icon on your minimap to find the furnace.")
        objbox(
            MiningTutorObjs.coal,
            "You'll need to be a fairly good miner before you can start " +
                "mining coal. You can find it in the Dwarf mine, Al Kharid " +
                "and quite a few other places.",
        )
        afterRockMenu()
    }

    private suspend fun Dialogue.silverAndGold() {
        objbox(
            MiningTutorObjs.silver_ore,
            "Silver can be mined to help you with your crafting. You " +
                "can find it in the Crafting guild, Al Kharid and many " +
                "other mines.",
        )
        objbox(
            MiningTutorObjs.gold_ore,
            "Gold requires quite a lot of experience in mining before " +
                "you can successfully extract it from the rocks. It can be " +
                "found in Al Kharid, the Crafting guild, Rimmington and " +
                "other places.",
        )
        afterRockMenu()
    }

    private suspend fun Dialogue.mithrilAdamantiteAndRunite() {
        doubleobjbox(
            MiningTutorObjs.mithril_ore,
            MiningTutorObjs.adamantite_ore,
            "Mithril and Adamantite rocks are fairly rare and require " +
                "great skill to mine. You can find them in Al Kharid, the " +
                "Dwarven mines, Lumbridge Swamps and other places.",
        )
        objbox(
            MiningTutorObjs.runite_ore,
            "Runite is the most prized of ores and is very rare. I'll " +
                "leave you to find where to mine it.",
        )
        afterRockMenu()
    }

    private suspend fun Dialogue.pickaxesMenu() {
        val choice =
            choice3(
                "Bronze, Iron and Steel.",
                1,
                "Mithril, Adamantite and Runite.",
                2,
                "Teach me about mining.",
                3,
            )
        when (choice) {
            1 -> lowTierPickaxes()
            2 -> highTierPickaxes()
            3 -> mainMenu()
        }
    }

    private suspend fun Dialogue.lowTierPickaxes() {
        objbox(
            objs.bronze_pickaxe,
            "The Bronze Pick is your basic tool of the trade. You can " +
                "buy one from Bob's Axe shop very cheaply, that's north " +
                "of here in Lumbridge, I also heard that exploring the " +
                "castle walls may yield a surprise or two.",
        )
        doubleobjbox(
            MiningTutorObjs.iron_pickaxe,
            MiningTutorObjs.steel_pickaxe,
            "The Iron and Steel Picks are faster than the bronze " +
                "pickaxe, but you will need to progress with your mining " +
                "a little before you can use them. You can buy them from " +
                "Nurmof in the Dwarven mines.",
        )
        pickaxesMenu()
    }

    private suspend fun Dialogue.highTierPickaxes() {
        doubleobjbox(
            MiningTutorObjs.mithril_pickaxe,
            MiningTutorObjs.rune_pickaxe,
            "Mithril, Adamantite, and Rune Picks may be used as you " +
                "progress with your skill of mining. They can be obtained " +
                "from Nurmof in the Dwarven mines.",
        )
        pickaxesMenu()
    }

    private suspend fun Dialogue.goodbye() {
        chatPlayer(neutral, "Goodbye.")
    }

    private companion object {
        val PICKAXES by lazy {
            listOf(
                objs.bronze_pickaxe,
                MiningTutorObjs.iron_pickaxe,
                MiningTutorObjs.steel_pickaxe,
                MiningTutorObjs.black_pickaxe,
                MiningTutorObjs.mithril_pickaxe,
                MiningTutorObjs.adamant_pickaxe,
                MiningTutorObjs.rune_pickaxe,
                MiningTutorObjs.dragon_pickaxe,
                MiningTutorObjs.dragon_pickaxe_or,
                MiningTutorObjs.infernal_pickaxe,
                MiningTutorObjs.infernal_pickaxe_uncharged,
                MiningTutorObjs.third_age_pickaxe,
                MiningTutorObjs.gilded_pickaxe,
                MiningTutorObjs.crystal_pickaxe,
            )
        }
    }
}

internal object MiningTutorObjs : ObjReferences() {
    val mining_site_icon = find("mining_site_icon_dummy")
    val clay = find("clay")
    val rune_essence = find("blankrune")
    val copper_ore = find("copper_ore")
    val tin_ore = find("tin_ore")
    val iron_ore = find("iron_ore")
    val coal = find("coal")
    val silver_ore = find("silver_ore")
    val gold_ore = find("gold_ore")
    val mithril_ore = find("mithril_ore")
    val adamantite_ore = find("adamantite_ore")
    val runite_ore = find("runite_ore")

    val iron_pickaxe = find("iron_pickaxe")
    val steel_pickaxe = find("steel_pickaxe")
    val black_pickaxe = find("black_pickaxe")
    val mithril_pickaxe = find("mithril_pickaxe")
    val adamant_pickaxe = find("adamant_pickaxe")
    val rune_pickaxe = find("rune_pickaxe")
    val dragon_pickaxe = find("dragon_pickaxe")
    val dragon_pickaxe_or = find("dragon_pickaxe_pretty")
    val infernal_pickaxe = find("infernal_pickaxe")
    val infernal_pickaxe_uncharged = find("infernal_pickaxe_empty")
    val third_age_pickaxe = find("3a_pickaxe")
    val gilded_pickaxe = find("trail_gilded_pickaxe")
    val crystal_pickaxe = find("crystal_pickaxe")
}
