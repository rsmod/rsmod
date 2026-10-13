package org.runerealm.content.skills.magic.teleports

import jakarta.inject.Inject
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.script.onOpHeld1
import org.runerealm.api.script.onOpHeld2
import org.runerealm.api.script.onOpHeld3
import org.runerealm.api.script.onOpHeld4
import org.runerealm.api.script.onOpHeld5
import org.runerealm.game.type.obj.ObjType
import org.runerealm.game.type.obj.ObjTypeList
import org.runerealm.map.CoordGrid
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

/**
 * Teleport tablets: "Break" uses up the tablet and teleports to its spell's destination, following
 * the OSRS Wiki (Teleport tablet). Tablets work up to level 20 Wilderness.
 */
class TeleportTablets @Inject constructor(private val objTypes: ObjTypeList) : PluginScript() {
    override fun ScriptContext.startup() {
        for ((name, dest) in TABLETS) {
            val type = objTypes.values.firstOrNull { it.internalName == name } ?: continue
            val breakOp = type.iop.indexOfFirst { it == "Break" } + 1
            val action: suspend ProtectedAccess.(Int) -> Unit = { slot ->
                breakTablet(type, slot, dest)
            }
            when (breakOp) {
                1 -> onOpHeld1(type) { action(it.slot) }
                2 -> onOpHeld2(type) { action(it.slot) }
                3 -> onOpHeld3(type) { action(it.slot) }
                4 -> onOpHeld4(type) { action(it.slot) }
                5 -> onOpHeld5(type) { action(it.slot) }
            }
        }
        val house = objTypes.values.firstOrNull { it.internalName == HOUSE_TABLET }
        if (house != null) {
            onOpHeld1(house) { mes("You don't have a house to teleport to.") }
        }
    }

    private suspend fun ProtectedAccess.breakTablet(type: ObjType, slot: Int, dest: CoordGrid) {
        if (player.inv[slot]?.id != type.id) {
            return
        }
        if (!ItemTeleports.canTeleport(this, ItemTeleports.TABLET_WILDERNESS_LIMIT)) {
            return
        }
        invDel(player.inv, type, count = 1, slot = slot)
        ItemTeleports.tabletTeleport(this, dest)
    }

    private companion object {
        const val HOUSE_TABLET = "poh_tablet_teleporttohouse"
    }
}

private fun coord(x: Int, z: Int, level: Int = 0) = CoordGrid(x, z, level)

/**
 * Tablet destinations. Varrock, Camelot and Watchtower use the wiki's map markers; the others use
 * the standard landing tiles of their teleport spells.
 */
internal val TABLETS: Map<String, CoordGrid> =
    mapOf(
        "poh_tablet_varrockteleport" to coord(3213, 3424),
        "poh_tablet_lumbridgeteleport" to coord(3222, 3218),
        "poh_tablet_faladorteleport" to coord(2965, 3378),
        "poh_tablet_camelotteleport" to coord(2757, 3479),
        "poh_tablet_ardougneteleport" to coord(2662, 3305),
        "poh_tablet_watchtowerteleport" to coord(2546, 3114, 2),
        "nzone_teletab_rimmington" to coord(2954, 3224),
        "nzone_teletab_taverley" to coord(2894, 3465),
        "nzone_teletab_pollnivneach" to coord(3340, 3004),
        "nzone_teletab_rellekka" to coord(2668, 3631),
        "nzone_teletab_brimhaven" to coord(2758, 3178),
        "nzone_teletab_yanille" to coord(2544, 3095),
        "nzone_teletab_trollheim" to coord(2910, 3612),
        "tablet_paddewa" to coord(3098, 9883),
        "tablet_senntisten" to coord(3321, 3335),
        "tablet_kharyll" to coord(3492, 3471),
        "tablet_lassar" to coord(3006, 3471),
        "tablet_dareeyak" to coord(2966, 3696),
        "tablet_carrallangar" to coord(3156, 3666),
        "tablet_annakarl" to coord(3288, 3886),
        "tablet_ghorrock" to coord(2977, 3873),
    )
