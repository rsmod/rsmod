package org.runerealm.content.skills.crafting.scripts

import jakarta.inject.Inject
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.script.onOpLoc1
import org.runerealm.api.script.onOpLoc2
import org.runerealm.api.script.onOpLoc3
import org.runerealm.api.script.onOpLoc4
import org.runerealm.api.script.onOpLoc5
import org.runerealm.api.script.onOpLocU
import org.runerealm.content.skills.crafting.configs.CraftRecipe
import org.runerealm.content.skills.crafting.configs.CraftingRecipes
import org.runerealm.content.skills.crafting.configs.crafting_objs
import org.runerealm.game.type.loc.LocTypeList
import org.runerealm.game.type.loc.UnpackedLocType
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

/**
 * Crafting at scenery, per the OSRS Wiki ([Crafting](https://oldschool.runescape.wiki/w/Crafting)):
 * - Spinning wheels: wool, flax, magic roots and sinew.
 * - Potter's wheels ("Use"): soft clay into unfired pottery; pottery ovens fire it.
 * - Furnaces: a bucket of sand and soda ash into molten glass.
 *
 * Furnace "Smelt" (and ore on furnaces) belongs to Smithing; jewellery is in [JewelleryCrafting].
 */
class CraftingStations
@Inject
constructor(private val locTypes: LocTypeList, private val production: CraftingProduction) :
    PluginScript() {
    override fun ScriptContext.startup() {
        for (wheel in CraftingLocs.spinningWheels(locTypes)) {
            onOp(wheel, "Spin") { spin() }
            for (recipe in CraftingRecipes.spinning) {
                onOpLocU(wheel, recipe.inputs.single().obj) {
                    production.start(this, listOf(recipe), SPIN_TITLE)
                }
            }
        }

        for (wheel in CraftingLocs.potteryWheels(locTypes)) {
            onOp(wheel, "Use") { form() }
            onOpLocU(wheel, crafting_objs.soft_clay) { form() }
        }

        for (oven in CraftingLocs.potteryOvens(locTypes)) {
            onOp(oven, "Fire") { fire(CraftingRecipes.potteryOven) }
            for (recipe in CraftingRecipes.potteryOven) {
                onOpLocU(oven, recipe.inputs.single().obj) { fire(listOf(recipe)) }
            }
        }

        for (furnace in CraftingLocs.furnaces(locTypes)) {
            onOpLocU(furnace, crafting_objs.bucket_sand) { moltenGlass() }
            onOpLocU(furnace, crafting_objs.soda_ash) { moltenGlass() }
        }
    }

    /** Registers [action] on whichever option of [type] is named [op]. */
    private fun ScriptContext.onOp(
        type: UnpackedLocType,
        op: String,
        action: suspend ProtectedAccess.() -> Unit,
    ) {
        when (type.op.indexOf(op)) {
            0 -> onOpLoc1(type) { action() }
            1 -> onOpLoc2(type) { action() }
            2 -> onOpLoc3(type) { action() }
            3 -> onOpLoc4(type) { action() }
            4 -> onOpLoc5(type) { action() }
        }
    }

    private suspend fun ProtectedAccess.spin() {
        production.start(this, CraftingRecipes.spinning, SPIN_TITLE)
    }

    private suspend fun ProtectedAccess.form() {
        if (crafting_objs.soft_clay !in inv) {
            mes("You need some soft clay to make pottery.")
            return
        }
        production.start(this, CraftingRecipes.potteryWheel, POTTERY_TITLE)
    }

    private suspend fun ProtectedAccess.fire(options: List<CraftRecipe>) {
        val carried = options.filter { recipe -> recipe.inputs.all { it.obj in inv } }
        if (carried.isEmpty()) {
            mes("You have nothing to fire in the oven.")
            return
        }
        production.start(this, carried, FIRE_TITLE)
    }

    private suspend fun ProtectedAccess.moltenGlass() {
        production.start(this, listOf(CraftingRecipes.moltenGlass), GLASS_TITLE)
    }

    private companion object {
        const val SPIN_TITLE = "What would you like to spin?"
        const val POTTERY_TITLE = "What would you like to make?"
        const val FIRE_TITLE = "What would you like to fire?"
        const val GLASS_TITLE = "How many would you like to make?"
    }
}

/** Crafting scenery, found by their cache name and options. */
internal object CraftingLocs {
    fun spinningWheels(types: LocTypeList): List<UnpackedLocType> =
        types.find("Spinning wheel", "Spin")

    fun potteryWheels(types: LocTypeList): List<UnpackedLocType> =
        types.find("Potter's Wheel", "Use")

    fun potteryOvens(types: LocTypeList): List<UnpackedLocType> = types.find("Pottery Oven", "Fire")

    fun furnaces(types: LocTypeList): List<UnpackedLocType> = types.find("Furnace", "Smelt")

    private fun LocTypeList.find(name: String, op: String): List<UnpackedLocType> =
        values.filter { it.name.equals(name, ignoreCase = true) && op in it.op }
}
