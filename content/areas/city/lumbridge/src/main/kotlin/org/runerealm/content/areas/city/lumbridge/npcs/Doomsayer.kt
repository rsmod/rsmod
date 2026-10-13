package org.runerealm.content.areas.city.lumbridge.npcs

import org.runerealm.api.player.dialogue.Dialogue
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.player.vars.resyncVar
import org.runerealm.api.script.onIfModalButton
import org.runerealm.api.script.onOpNpc1
import org.runerealm.api.script.onOpNpc3
import org.runerealm.api.type.refs.comp.ComponentReferences
import org.runerealm.api.type.refs.interf.InterfaceReferences
import org.runerealm.api.type.refs.varbit.VarBitReferences
import org.runerealm.content.areas.city.lumbridge.configs.lumbridge_npcs
import org.runerealm.game.entity.Npc
import org.runerealm.game.type.comp.ComponentType
import org.runerealm.game.type.varbit.VarBitType
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

/**
 * The [Doomsayer](https://oldschool.runescape.wiki/w/Doomsayer) and his "Reset Warning Messages"
 * interface (`cws_doomsayer`).
 *
 * Each warning screen ("cws" warning) has a `cws_warning_N` varbit that counts how often the
 * warning has been seen. Once seen enough times the varbit reaches [WARNING_TOGGLE] and the player
 * may turn it off, which sets it to [WARNING_DISABLED]. The Doomsayer's interface lets players flip
 * unlocked warnings between those two states; the client-side `cws_graphic_swap` script reads the
 * varbits to draw each button as enabled (6), disabled (7) or locked (anything else).
 */
class Doomsayer : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(lumbridge_npcs.doomsayer) { doomsayerDialogue(it.npc) }
        onOpNpc3(lumbridge_npcs.doomsayer) { openWarnings() }
        onIfModalButton(doomsayer_components.close_button) { ifClose() }
        for ((component, varbit) in warningToggles) {
            onIfModalButton(component) { toggleWarning(varbit) }
        }
    }

    private suspend fun ProtectedAccess.doomsayerDialogue(npc: Npc) {
        var openInterface = false
        startDialogue(npc) { openInterface = dooooom() }
        if (openInterface) {
            openWarnings()
        }
    }

    private fun ProtectedAccess.openWarnings() {
        ifOpenMainModal(doomsayer_interfaces.warnings)
        // The button graphics are only drawn on var transmit, so resend the warning varps.
        for (varbit in warningToggles.values) {
            player.resyncVar(varbit)
        }
    }

    private fun ProtectedAccess.toggleWarning(varbit: VarBitType) {
        when (vars[varbit]) {
            WARNING_TOGGLE -> vars[varbit] = WARNING_DISABLED
            WARNING_DISABLED -> vars[varbit] = WARNING_TOGGLE
            else ->
                mes(
                    "You cannot toggle this warning screen on or off. You need to go to the area " +
                        "it is linked to enough times to have the option to do so."
                )
        }
    }
}

private const val WARNING_TOGGLE = 6
private const val WARNING_DISABLED = 7

/** @return `true` if the player asked to turn warnings back on. */
private suspend fun Dialogue.dooooom(): Boolean {
    chatNpc(angry, "Dooooom!")
    chatPlayer(confused, "Where?")
    chatNpc(
        angry,
        "All around us! I can feel it in the air, hear it on the wind, smell it... also in " +
            "the air!",
    )
    chatPlayer(quiz, "Is there anything we can do about this doom?")
    chatNpc(
        neutral,
        "There is nothing you need to do my friend! I am the Doomsayer, although my real " +
            "title could be something like the Danger Tutor.",
    )
    chatPlayer(confused, "Danger Tutor?")
    chatNpc(happy, "Yes! I roam the world sensing danger.")
    chatNpc(
        neutral,
        "If you see the signs often enough, then you can turn them off; by that time you " +
            "likely know what the area has in store for you.",
    )
    chatNpc(
        neutral,
        "If I find a dangerous area, then I put up warning signs that will tell you what is " +
            "so dangerous about that area.",
    )
    chatPlayer(quiz, "But what if I want to see the warnings again?")
    chatNpc(laugh, "That's why I'm waiting here!")
    chatNpc(
        neutral,
        "If you want to see the warning messages again, I can turn them back on for you.",
    )
    val disabledAny = warningToggles.values.any { vars[it] == WARNING_DISABLED }
    if (!disabledAny) {
        chatPlayer(happy, "Thanks, I'll remember that if I see any warning messages.")
        chatNpc(happy, "You're welcome!")
        return false
    }
    chatNpc(quiz, "Do you need to turn on any warnings right now?")
    val turnOn = choice2("Yes, I do.", true, "Not right now.", false)
    if (turnOn) {
        chatPlayer(happy, "Yes, I do.")
        return true
    }
    chatPlayer(neutral, "Not right now.")
    chatNpc(neutral, "Ok, keep an eye out for the messages though!")
    chatPlayer(neutral, "I will.")
    return false
}

/** Maps each "Toggle" text component of the interface to the warning varbit it controls. */
private val warningToggles: Map<ComponentType, VarBitType> by lazy {
    with(doomsayer_components) {
        with(doomsayer_varbits) {
            mapOf(
                dagannoth_kings_ladder to warning_1,
                goblin_cave_rope to warning_13,
                stronghold_of_security_ladders to warning_4,
                giant_mole_area to warning_3,
                random_events to warning_6,
                player_owned_houses to warning_5,
                contact_dungeon_ladder to warning_2,
                ice_troll_area to warning_11,
                ham_tunnel to warning_14,
                fairy_rings to warning_15,
                lumbridge_cellar to warning_16,
                mort_myre to warning_20,
                observatory_stairs to warning_9,
                shantay_pass to warning_17,
                genie_cave to warning_10,
                watchtower_shaman_cave to warning_12,
                trollheim_wilderness_entrance to warning_8,
                wilderness_ditch to warning_7,
                dorgesh_kaan_exit to warning_19,
                dorgesh_kaan_tunnel to warning_18,
                ranging_guild_tower to warning_23,
                death_plateau to warning_24,
                godwars_area to warning_25,
                wilderness_ditch_high_risk to warning_26,
            )
        }
    }
}

internal object doomsayer_interfaces : InterfaceReferences() {
    val warnings = find("cws_doomsayer")
}

internal object doomsayer_components : ComponentReferences() {
    val close_button = find("cws_doomsayer:close_button")
    val dagannoth_kings_ladder = find("cws_doomsayer:dagganoth kings ladder")
    val goblin_cave_rope = find("cws_doomsayer:goblin cave rope")
    val stronghold_of_security_ladders = find("cws_doomsayer:stronghold of security ladders")
    val giant_mole_area = find("cws_doomsayer:giant mole area")
    val random_events = find("cws_doomsayer:anti macro events")
    val player_owned_houses = find("cws_doomsayer:player owned houses")
    val contact_dungeon_ladder = find("cws_doomsayer:contact dungeon ladder")
    val ice_troll_area = find("cws_doomsayer:ice troll area")
    val ham_tunnel = find("cws_doomsayer:ham tunnel")
    val fairy_rings = find("cws_doomsayer:fairy rings")
    val lumbridge_cellar = find("cws_doomsayer:lumbridge cellar")
    val mort_myre = find("cws_doomsayer:mort myre")
    val observatory_stairs = find("cws_doomsayer:observatory stairs")
    val shantay_pass = find("cws_doomsayer:shantay pass")
    val genie_cave = find("cws_doomsayer:genie cave")
    val watchtower_shaman_cave = find("cws_doomsayer:watchtower shaman cave")
    val trollheim_wilderness_entrance = find("cws_doomsayer:trolheim wildy entrance")
    val wilderness_ditch = find("cws_doomsayer:wilderness ditch")
    val dorgesh_kaan_exit = find("cws_doomsayer:exits from dorgesh kaahn")
    val dorgesh_kaan_tunnel = find("cws_doomsayer:exits from dorgesh kaahn 2")
    val ranging_guild_tower = find("cws_doomsayer:range guild tower")
    val death_plateau = find("cws_doomsayer:death plateau")
    val godwars_area = find("cws_doomsayer:godwars area")
    val wilderness_ditch_high_risk = find("cws_doomsayer:wilderness ditch highrisk")
}

internal object doomsayer_varbits : VarBitReferences() {
    val warning_1 = find("cws_warning_1")
    val warning_2 = find("cws_warning_2")
    val warning_3 = find("cws_warning_3")
    val warning_4 = find("cws_warning_4")
    val warning_5 = find("cws_warning_5")
    val warning_6 = find("cws_warning_6")
    val warning_7 = find("cws_warning_7")
    val warning_8 = find("cws_warning_8")
    val warning_9 = find("cws_warning_9")
    val warning_10 = find("cws_warning_10")
    val warning_11 = find("cws_warning_11")
    val warning_12 = find("cws_warning_12")
    val warning_13 = find("cws_warning_13")
    val warning_14 = find("cws_warning_14")
    val warning_15 = find("cws_warning_15")
    val warning_16 = find("cws_warning_16")
    val warning_17 = find("cws_warning_17")
    val warning_18 = find("cws_warning_18")
    val warning_19 = find("cws_warning_19")
    val warning_20 = find("cws_warning_20")
    val warning_23 = find("cws_warning_23")
    val warning_24 = find("cws_warning_24")
    val warning_25 = find("cws_warning_25")
    val warning_26 = find("cws_warning_26")
}
