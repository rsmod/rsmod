package org.runerealm.api.death.plugin

import jakarta.inject.Inject
import org.runerealm.api.death.DeathValues
import org.runerealm.api.death.Gravestones
import org.runerealm.api.death.configs.death_locs
import org.runerealm.api.death.configs.death_npcs
import org.runerealm.api.player.dialogue.Dialogue
import org.runerealm.api.player.ironman.isIronman
import org.runerealm.api.player.ironman.isUltimateIronman
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.script.onOpLoc1
import org.runerealm.api.script.onOpNpc1
import org.runerealm.api.script.onOpNpc3
import org.runerealm.game.entity.Npc
import org.runerealm.game.type.obj.ObjTypeList
import org.runerealm.map.CoordGrid
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

/**
 * [Death's Office](https://oldschool.runescape.wiki/w/Death%27s_Office): Death holds the items from
 * graves that ran out of time. Reclaiming them is free for items worth under 100k and costs 5% of
 * the value of anything worth more (2.5% for ironmen). Death can also fetch the items from a grave
 * that is still standing, at the same (higher) fee.
 *
 * The office is entered through the "Death's Domain" entrances near respawn points, and left
 * through the portal.
 */
public class DeathOfficeScript
@Inject
constructor(
    private val gravestones: Gravestones,
    private val values: DeathValues,
    private val objTypes: ObjTypeList,
) : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(death_npcs.death) { talkToDeath(it.npc) }
        onOpNpc3(death_npcs.death) { collect() }
        onOpLoc1(death_locs.access_browngrave) { enterOffice() }
        onOpLoc1(death_locs.access_coffin) { enterOffice() }
        onOpLoc1(death_locs.access_whitecoffin) { enterOffice() }
        onOpLoc1(death_locs.exit_portal) { telejump(OFFICE_EXIT) }
    }

    private suspend fun ProtectedAccess.talkToDeath(npc: Npc) =
        startDialogue(npc) {
            chatNpc(neutral, "Greetings, mortal. What brings you to my office?")
            val collect =
                choice2(
                    "I'd like to collect my items.",
                    true,
                    "Nothing, thanks.",
                    false,
                    title = "What would you like to say?",
                )
            if (collect) {
                chatPlayer(neutral, "I'd like to collect my items.")
                collectFromDialogue()
            } else {
                chatPlayer(neutral, "Nothing, thanks.")
            }
        }

    private suspend fun Dialogue.collectFromDialogue() {
        if (!gravestones.hasGrave(player) && gravestones.deathOfficeInv(player).isEmpty()) {
            chatNpc(neutral, "I'm not holding on to anything of yours.")
            return
        }
        access.collect()
    }

    private fun ProtectedAccess.collect() {
        if (gravestones.hasGrave(player)) {
            // Death fetches the items from a grave that is still standing.
            gravestones.moveToDeathOffice(player)
            gravestones.remove(player)
        }
        val office = gravestones.deathOfficeInv(player)
        if (office.isEmpty()) {
            mes("Death isn't holding on to any of your items.")
            return
        }
        val ironman = player.isIronman && !player.isUltimateIronman
        reclaim(gravestones, objTypes, office) { objs -> values.deathOfficeFee(objs, ironman) }
            ?: return
        if (office.isEmpty()) {
            mes("You have reclaimed all of your items from Death.")
        } else {
            mes("You don't have enough inventory space to reclaim everything from Death.")
        }
    }

    private fun ProtectedAccess.enterOffice() {
        telejump(OFFICE_ARRIVAL)
    }

    private companion object {
        /** Just east of the exit portal (wiki map: portal at 3169, 5726). */
        val OFFICE_ARRIVAL = CoordGrid(0, 49, 89, 38, 30)
        /** Lumbridge, the default respawn point. */
        val OFFICE_EXIT = CoordGrid(0, 50, 50, 21, 18)
    }
}
