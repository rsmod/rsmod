package org.runerealm.content.skills.cooking.scripts

import jakarta.inject.Inject
import org.runerealm.api.config.constants
import org.runerealm.api.config.refs.stats
import org.runerealm.api.config.refs.varps
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.player.stat.cookingLvl
import org.runerealm.api.repo.loc.LocRepository
import org.runerealm.api.repo.npc.NpcRepository
import org.runerealm.api.script.onOpLoc1
import org.runerealm.api.script.onOpLocU
import org.runerealm.api.script.onPlayerQueueWithArgs
import org.runerealm.api.stats.levelmod.InvisibleLevels
import org.runerealm.api.stats.xpmod.XpModifiers
import org.runerealm.content.skills.cooking.configs.CookRate
import org.runerealm.content.skills.cooking.configs.CookingCategories
import org.runerealm.content.skills.cooking.configs.CookingLocs
import org.runerealm.content.skills.cooking.configs.CookingNpcs
import org.runerealm.content.skills.cooking.configs.CookingQueues
import org.runerealm.content.skills.cooking.configs.CookingRecipe
import org.runerealm.content.skills.cooking.configs.CookingRecipes
import org.runerealm.content.skills.cooking.configs.CookingSeqs
import org.runerealm.events.UnboundEvent
import org.runerealm.game.entity.Player
import org.runerealm.game.loc.BoundLocInfo
import org.runerealm.game.type.loc.LocTypeList
import org.runerealm.game.type.loc.UnpackedLocType
import org.runerealm.game.type.obj.UnpackedObjType
import org.runerealm.map.zone.ZoneKey
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

// TODO:
// - cooking gauntlets / cooking cape (members)
// - Hosidius kitchen range bonus (members)
// - sinew from raw beef/bear meat on a range (members)
// - cooking food other than fish and meat (bread, pies, stews, etc.)
class Cooking
@Inject
constructor(
    private val locTypes: LocTypeList,
    private val locRepo: LocRepository,
    private val npcRepo: NpcRepository,
    private val xpMods: XpModifiers,
    private val invisibleLvls: InvisibleLevels,
) : PluginScript() {
    private val recipes = CookingRecipes.all.associateBy { it.raw.id }

    override fun ScriptContext.startup() {
        for (type in heatSourceTypes()) {
            onOpLocU(type) { useOnHeatSource(it.loc, it.type, it.objType) }
            if (type.op.getOrNull(0) == "Cook") {
                onOpLoc1(type) { cookOnHeatSource(it.loc, it.type) }
            }
        }
        onPlayerQueueWithArgs<CookTask>(CookingQueues.cook) { cookQueue(it.args) }
    }

    /** All fires and ranges, as grouped by their cache category, plus the Lumbridge range. */
    private fun heatSourceTypes(): List<UnpackedLocType> {
        val categories = setOf(CookingCategories.fire.id, CookingCategories.range.id)
        val types = locTypes.values.filter { it.category in categories }.toMutableList()
        val lumbridge = locTypes.getValue(CookingLocs.lumbridge_range.id)
        if (lumbridge !in types) {
            types += lumbridge
        }
        return types
    }

    private suspend fun ProtectedAccess.useOnHeatSource(
        loc: BoundLocInfo,
        type: UnpackedLocType,
        obj: UnpackedObjType,
    ) {
        val recipe = recipes[obj.id]
        if (recipe == null) {
            mes(constants.dm_default)
            return
        }
        startCooking(loc, type, listOf(recipe))
    }

    private suspend fun ProtectedAccess.cookOnHeatSource(loc: BoundLocInfo, type: UnpackedLocType) {
        val carried = inv.mapNotNull { obj -> obj?.let { recipes[it.id] } }.distinct()
        if (carried.isEmpty()) {
            // TODO: Verify message.
            mes("You have nothing to cook.")
            return
        }
        startCooking(loc, type, carried.take(MAX_OPTIONS))
    }

    private suspend fun ProtectedAccess.startCooking(
        loc: BoundLocInfo,
        type: UnpackedLocType,
        options: List<CookingRecipe>,
    ) {
        val heat = type.heatSource()
        if (heat == HeatSource.LumbridgeRange && vars[varps.cookquest] < COOKS_ASSISTANT_COMPLETE) {
            lumbridgeCookStop(loc)
            return
        }

        val counts = options.map { invTotal(inv, it.raw) }
        val (recipe, quantity) =
            if (options.size == 1 && counts.single() == 1) {
                // The cooking interface is skipped when only one raw food is held.
                options.single() to 1
            } else {
                val selection =
                    skillMulti(
                        title = "What would you like to cook?",
                        choices = options.map { it.raw },
                        maxQuantity = counts.max(),
                    )
                val selected = options.first { it.raw.id == selection.obj.id }
                selected to selection.quantity
            }

        if (player.cookingLvl < recipe.level) {
            // TODO: Verify message.
            mes("You need a Cooking level of ${recipe.level} to cook this.")
            return
        }

        val task = CookTask(loc, heat, recipe, remaining = quantity, cooked = 0)
        if (quantity == 1 && options.size == 1 && counts.single() == 1) {
            cook(task)
        } else {
            weakQueue(CookingQueues.cook, FIRST_COOK_DELAY, task)
        }
    }

    private suspend fun ProtectedAccess.lumbridgeCookStop(loc: BoundLocInfo) {
        val cook =
            npcRepo.findAll(ZoneKey.from(loc.coords), zoneRadius = 1).firstOrNull {
                it.id == CookingNpcs.lumbridge_cook.id
            }
        if (cook == null) {
            mes("Hey, who said you could use that?")
            return
        }
        startDialogue(cook) { chatNpc(angry, "Hey, who said you could use that?") }
    }

    private fun ProtectedAccess.cookQueue(task: CookTask) {
        cook(task)
    }

    private fun ProtectedAccess.cook(task: CookTask) {
        val loc = locRepo.findExact(task.loc.coords, locTypes[task.loc])
        if (loc == null) {
            // The fire burned out.
            return
        }

        val recipe = task.recipe
        val slot = inv.indices.firstOrNull { inv[it]?.id == recipe.raw.id } ?: return

        anim(if (task.heat == HeatSource.Fire) CookingSeqs.fire else CookingSeqs.range)

        val rate = recipe.rate(task.heat)
        val success = statRandom(stats.cooking, rate.low, rate.high, invisibleLvls)
        if (success) {
            invReplaceSlot(inv, slot, count = 1, replacement = recipe.cooked)
            statAdvance(stats.cooking, recipe.xp * xpMods.get(player, stats.cooking))
            spam(recipe.successMessage)
        } else {
            invReplaceSlot(inv, slot, count = 1, replacement = recipe.burnt)
            spam(recipe.burnMessage)
        }
        publish(CookFood(player, recipe, burnt = !success))

        val next = task.copy(remaining = task.remaining - 1, cooked = task.cooked + 1)
        if (next.remaining <= 0 || recipe.raw !in inv) {
            return
        }
        val delay = if (next.cooked == 1) SECOND_COOK_DELAY else COOK_DELAY
        weakQueue(CookingQueues.cook, delay, next)
    }

    private fun UnpackedLocType.heatSource(): HeatSource =
        when {
            id == CookingLocs.lumbridge_range.id -> HeatSource.LumbridgeRange
            category == CookingCategories.fire.id -> HeatSource.Fire
            else -> HeatSource.Range
        }

    private fun CookingRecipe.rate(heat: HeatSource): CookRate =
        when (heat) {
            HeatSource.Fire -> fire
            HeatSource.Range -> range
            HeatSource.LumbridgeRange -> lumbridgeRange
        }

    enum class HeatSource {
        Fire,
        Range,
        LumbridgeRange,
    }

    data class CookTask(
        val loc: BoundLocInfo,
        val heat: HeatSource,
        val recipe: CookingRecipe,
        val remaining: Int,
        val cooked: Int,
    )

    data class CookFood(val player: Player, val recipe: CookingRecipe, val burnt: Boolean) :
        UnboundEvent

    private companion object {
        /** Quest `endstate` from the `quest_cooksassistant` dbrow. */
        const val COOKS_ASSISTANT_COMPLETE = 2

        /** Max options displayed by the skillmulti interface. */
        const val MAX_OPTIONS = 10

        // "1 tick for the first meat, 3 ticks for the second, then 4 ticks for the rest."
        // (OSRS Wiki, "Cooked meat")
        //
        // Weak queues are decremented on the same cycle they are queued, so a queue with a delay
        // of `n` executes `n - 1` cycles later.
        const val FIRST_COOK_DELAY = 2
        const val SECOND_COOK_DELAY = 4
        const val COOK_DELAY = 5
    }
}
