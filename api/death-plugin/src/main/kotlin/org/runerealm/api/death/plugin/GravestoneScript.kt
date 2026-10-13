package org.runerealm.api.death.plugin

import jakarta.inject.Inject
import org.runerealm.api.death.DeathValues
import org.runerealm.api.death.Gravestones
import org.runerealm.api.death.configs.death_npcs
import org.runerealm.api.death.configs.death_timers
import org.runerealm.api.player.ironman.isIronman
import org.runerealm.api.player.ironman.isUltimateIronman
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.script.onOpNpc1
import org.runerealm.api.script.onOpNpc3
import org.runerealm.api.script.onPlayerLogin
import org.runerealm.api.script.onPlayerLogout
import org.runerealm.api.script.onPlayerSoftTimer
import org.runerealm.game.entity.Npc
import org.runerealm.game.type.obj.ObjTypeList
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

/**
 * The [grave](https://oldschool.runescape.wiki/w/Grave): its timer (which only counts down while
 * the owner is logged in), and its "Check" and "Loot" options.
 */
public class GravestoneScript
@Inject
constructor(
    private val gravestones: Gravestones,
    private val values: DeathValues,
    private val objTypes: ObjTypeList,
) : PluginScript() {
    override fun ScriptContext.startup() {
        onPlayerSoftTimer(death_timers.gravestone) { gravestones.tick(player) }
        onPlayerLogin { gravestones.spawn(player) }
        onPlayerLogout { gravestones.logout(player) }
        onOpNpc1(death_npcs.gravestone) { checkGrave(it.npc) }
        onOpNpc3(death_npcs.gravestone) { lootGrave(it.npc) }
    }

    private fun ProtectedAccess.checkGrave(npc: Npc) {
        if (!gravestones.isOwner(player, npc)) {
            mes("Someone's been dead round here. Tut tut.")
            return
        }
        val minutes = minutesLeft(gravestones.ticksLeft(player))
        mes(
            "Your gravestone will last for another $minutes ${if (minutes == 1) "minute" else "minutes"}."
        )
    }

    private fun ProtectedAccess.lootGrave(npc: Npc) {
        if (!gravestones.isOwner(player, npc)) {
            mes("This isn't your gravestone.")
            return
        }
        val grave = gravestones.gravestoneInv(player)
        val ironman = player.isIronman && !player.isUltimateIronman
        val collected =
            reclaim(gravestones, objTypes, grave) { objs -> values.gravestoneFee(objs, ironman) }
                ?: return
        if (grave.isEmpty()) {
            gravestones.remove(player)
            mes("You have collected all of the items from your gravestone.")
        } else if (collected) {
            mes("You don't have enough inventory space to collect everything from your gravestone.")
        }
    }

    private fun minutesLeft(ticks: Int): Int =
        (ticks + Gravestones.TICKS_PER_MINUTE - 1) / Gravestones.TICKS_PER_MINUTE
}
