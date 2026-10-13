package org.runerealm.content.skills.magic.teleports

import jakarta.inject.Inject
import org.runerealm.api.config.constants
import org.runerealm.api.config.refs.params
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.script.onOpHeld1
import org.runerealm.api.script.onOpHeld2
import org.runerealm.api.script.onOpHeld3
import org.runerealm.api.script.onOpHeld4
import org.runerealm.api.script.onOpHeld5
import org.runerealm.api.script.onOpHeldSubOp
import org.runerealm.api.script.onOpWorn2
import org.runerealm.api.script.onOpWorn3
import org.runerealm.api.script.onOpWorn4
import org.runerealm.api.script.onOpWorn5
import org.runerealm.api.script.onOpWorn6
import org.runerealm.api.script.onOpWorn7
import org.runerealm.game.inv.Inventory
import org.runerealm.game.type.obj.ObjType
import org.runerealm.game.type.obj.ObjTypeList
import org.runerealm.game.type.obj.UnpackedObjType
import org.runerealm.map.CoordGrid
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

/**
 * Teleport jewellery following the OSRS Wiki item pages (Amulet of glory, Ring of dueling, Games
 * necklace, Combat bracelet, Skills necklace, Ring of wealth). Each teleport uses a charge; the
 * destinations' coordinates are the wiki's map markers.
 *
 * A teleport can be picked three ways, as in the client:
 * - "Rub" from the inventory, then a destination from the chat menu;
 * - the destination straight from the "Rub" right-click submenu (the obj's `isubop` entries, sent
 *   as an `IF_SUBOP` held sub-op);
 * - the destination's op on the worn item in the equipment tab (the obj's `wear_opN` params).
 */
class JewelleryTeleports @Inject constructor(private val objTypes: ObjTypeList) : PluginScript() {
    override fun ScriptContext.startup() {
        for (jewellery in JEWELLERY) {
            for ((index, charged) in jewellery.charges.withIndex()) {
                val type = objTypes.values.firstOrNull { it.internalName == charged } ?: continue
                val next = jewellery.charges.getOrNull(index + 1) ?: jewellery.empty
                registerRub(jewellery, type, next)
                registerWornOps(jewellery, type, next)
            }
        }
    }

    private fun ScriptContext.registerRub(
        jewellery: Jewellery,
        type: UnpackedObjType,
        next: String?,
    ) {
        val rubOp = type.iop.indexOfFirst { it == "Rub" } + 1
        if (rubOp !in 1..5) {
            return
        }
        val action: suspend ProtectedAccess.(Int) -> Unit = { slot ->
            val dest = chooseDestination(jewellery.destinations)
            if (dest != null) {
                teleport(jewellery, type, player.inv, slot, next, dest)
            }
        }
        when (rubOp) {
            1 -> onOpHeld1(type) { action(it.slot) }
            2 -> onOpHeld2(type) { action(it.slot) }
            3 -> onOpHeld3(type) { action(it.slot) }
            4 -> onOpHeld4(type) { action(it.slot) }
            5 -> onOpHeld5(type) { action(it.slot) }
        }
        onOpHeldSubOp(type) { event ->
            val dest = jewellery.destinations.getOrNull(event.subop - 1)
            if (event.op.slot != rubOp || dest == null) {
                // Other submenus (e.g. the ring of wealth's "Features") are not implemented.
                mes(constants.dm_default)
                return@onOpHeldSubOp
            }
            teleport(jewellery, type, player.inv, event.slot, next, dest)
        }
    }

    /**
     * Worn ops come from the obj's `wear_op1`.. params, which list the destinations in the same
     * order as the rub menu; `wear_opN` is sent as worn op `N + 1` (op 1 is "Remove").
     */
    private fun ScriptContext.registerWornOps(
        jewellery: Jewellery,
        type: UnpackedObjType,
        next: String?,
    ) {
        for ((index, dest) in jewellery.destinations.withIndex()) {
            val label = WEAR_OPS.getOrNull(index)?.let(type::paramOrNull) ?: break
            // The cache abbreviates a few labels (e.g. "Falador" for Falador Park).
            if (!dest.name.startsWith(label)) {
                continue
            }
            val action: suspend ProtectedAccess.(Int) -> Unit = { slot ->
                teleport(jewellery, type, player.worn, slot, next, dest)
            }
            when (index + 2) {
                2 -> onOpWorn2(type) { action(it.slot) }
                3 -> onOpWorn3(type) { action(it.slot) }
                4 -> onOpWorn4(type) { action(it.slot) }
                5 -> onOpWorn5(type) { action(it.slot) }
                6 -> onOpWorn6(type) { action(it.slot) }
                7 -> onOpWorn7(type) { action(it.slot) }
            }
        }
    }

    private suspend fun ProtectedAccess.teleport(
        jewellery: Jewellery,
        type: ObjType,
        inventory: Inventory,
        slot: Int,
        next: String?,
        dest: Destination,
    ) {
        if (inventory[slot]?.id != type.id) {
            return
        }
        if (!ItemTeleports.canTeleport(this, ItemTeleports.JEWELLERY_WILDERNESS_LIMIT)) {
            return
        }
        when {
            next == UNLIMITED -> {}
            next == null -> {
                invDel(inventory, type, count = 1, slot = slot)
                mes(jewellery.crumbleMessage ?: "Your ${jewellery.name} crumbles to dust.")
            }
            else -> {
                val nextType = objTypes.values.first { it.internalName == next }
                invReplaceSlot(inventory, slot, 1, nextType)
            }
        }
        ItemTeleports.jewelleryTeleport(this, dest.coords)
        jewellery.chargesMessage(next)?.let(::mes)
    }

    private companion object {
        val WEAR_OPS =
            listOf(
                params.wear_op1,
                params.wear_op2,
                params.wear_op3,
                params.wear_op4,
                params.wear_op5,
                params.wear_op6,
            )
    }

    private suspend fun ProtectedAccess.chooseDestination(
        destinations: List<Destination>
    ): Destination? {
        if (destinations.size <= 5) {
            return pick(destinations)
        }
        // More than five destinations: the first page offers four and "More...".
        val first = destinations.take(4)
        val choice = pick(first + Destination("More...", CoordGrid.NULL))
        if (choice?.name != "More...") {
            return choice
        }
        return pick(destinations.drop(4))
    }

    private suspend fun ProtectedAccess.pick(options: List<Destination>): Destination? =
        when (options.size) {
            1 -> options[0]
            2 -> choice2(options[0].name, options[0], options[1].name, options[1])
            3 ->
                choice3(
                    options[0].name,
                    options[0],
                    options[1].name,
                    options[1],
                    options[2].name,
                    options[2],
                )
            4 ->
                choice4(
                    options[0].name,
                    options[0],
                    options[1].name,
                    options[1],
                    options[2].name,
                    options[2],
                    options[3].name,
                    options[3],
                )
            else ->
                choice5(
                    options[0].name,
                    options[0],
                    options[1].name,
                    options[1],
                    options[2].name,
                    options[2],
                    options[3].name,
                    options[3],
                    options[4].name,
                    options[4],
                )
        }
}

data class Destination(val name: String, val coords: CoordGrid)

/**
 * A teleport jewellery family. [charges] lists the charged forms from most to fewest charges; using
 * the last charge turns it into [empty], or destroys it when [empty] is null.
 */
data class Jewellery(
    val name: String,
    val charges: List<String>,
    val empty: String?,
    val destinations: List<Destination>,
    val crumbleMessage: String? = null,
) {
    fun chargesMessage(next: String?): String? {
        if (next == UNLIMITED) {
            return null
        }
        val remaining =
            if (next == null || next == empty) 0 else charges.size - charges.indexOf(next)
        return when {
            next == null -> null
            remaining == 0 -> "You use your $name's last charge."
            remaining == 1 -> "Your $name has one charge left."
            else -> "Your $name has ${NUMBERS.getOrElse(remaining) { "$remaining" }} charges left."
        }
    }

    private companion object {
        val NUMBERS = listOf("zero", "one", "two", "three", "four", "five", "six", "seven", "eight")
    }
}

/** Marker for jewellery with unlimited charges (amulet of eternal glory). */
internal const val UNLIMITED = "unlimited"

private fun coord(x: Int, z: Int, level: Int = 0) = CoordGrid(x, z, level)

private fun numbered(prefix: String, max: Int): List<String> =
    (max downTo 1).map { "${prefix}_$it" }

val JEWELLERY: List<Jewellery> =
    listOf(
        Jewellery(
            name = "amulet of glory",
            charges = numbered("amulet_of_glory", 6),
            empty = "amulet_of_glory",
            destinations =
                listOf(
                    Destination("Edgeville", coord(3087, 3496)),
                    Destination("Karamja", coord(2918, 3176)),
                    Destination("Draynor Village", coord(3105, 3251)),
                    Destination("Al Kharid", coord(3293, 3163)),
                ),
        ),
        Jewellery(
            name = "amulet of eternal glory",
            charges = listOf("amulet_of_glory_inf"),
            empty = UNLIMITED,
            destinations =
                listOf(
                    Destination("Edgeville", coord(3087, 3496)),
                    Destination("Karamja", coord(2918, 3176)),
                    Destination("Draynor Village", coord(3105, 3251)),
                    Destination("Al Kharid", coord(3293, 3163)),
                ),
        ),
        Jewellery(
            name = "ring of dueling",
            charges = numbered("ring_of_dueling", 8),
            empty = null,
            destinations =
                listOf(
                    Destination("Emir's Arena", coord(3315, 3235)),
                    Destination("Castle Wars", coord(2440, 3090)),
                    Destination("Ferox Enclave", coord(3151, 3635)),
                    Destination("Fortis Colosseum", coord(1793, 3107)),
                ),
            crumbleMessage = "Your ring of dueling crumbles to dust.",
        ),
        Jewellery(
            name = "games necklace",
            charges = numbered("necklace_of_minigames", 8),
            empty = null,
            destinations =
                listOf(
                    Destination("Burthorpe", coord(2899, 3553)),
                    Destination("Barbarian Outpost", coord(2520, 3571)),
                    Destination("Corporeal Beast", coord(2967, 4254)),
                    Destination("Tears of Guthix", coord(3245, 9500)),
                    Destination("Wintertodt Camp", coord(1631, 3940)),
                ),
            crumbleMessage = "Your games necklace crumbles to dust.",
        ),
        Jewellery(
            name = "combat bracelet",
            charges = numbered("jewl_bracelet_of_combat", 6),
            empty = "jewl_bracelet_of_combat",
            destinations =
                listOf(
                    Destination("Warriors' Guild", coord(2882, 3547)),
                    Destination("Champions' Guild", coord(3192, 3368)),
                    Destination("Monastery", coord(3052, 3490)),
                    Destination("Ranging Guild", coord(2653, 3439)),
                ),
        ),
        Jewellery(
            name = "skills necklace",
            charges = numbered("jewl_necklace_of_skills", 6),
            empty = "jewl_necklace_of_skills",
            destinations =
                listOf(
                    Destination("Fishing Guild", coord(2611, 3390)),
                    Destination("Mining Guild", coord(3049, 9763)),
                    Destination("Crafting Guild", coord(2933, 3295)),
                    Destination("Cooking Guild", coord(3144, 3438)),
                    Destination("Woodcutting Guild", coord(1662, 3505)),
                    Destination("Farming Guild", coord(1248, 3719)),
                ),
        ),
        Jewellery(
            name = "ring of wealth",
            charges = numbered("ring_of_wealth", 5),
            empty = "ring_of_wealth",
            destinations =
                listOf(
                    Destination("Miscellania", coord(2534, 3862)),
                    Destination("Grand Exchange", coord(3163, 3478)),
                    Destination("Falador Park", coord(2995, 3375)),
                    Destination("Dondakan's Rock", coord(2824, 10168)),
                ),
        ),
        Jewellery(
            name = "ring of wealth (i)",
            charges = (5 downTo 1).map { "ring_of_wealth_i$it" },
            empty = "ring_of_wealth_i",
            destinations =
                listOf(
                    Destination("Miscellania", coord(2534, 3862)),
                    Destination("Grand Exchange", coord(3163, 3478)),
                    Destination("Falador Park", coord(2995, 3375)),
                    Destination("Dondakan's Rock", coord(2824, 10168)),
                ),
        ),
    )
