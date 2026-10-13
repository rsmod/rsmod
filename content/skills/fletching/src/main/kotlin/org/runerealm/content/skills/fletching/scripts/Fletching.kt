package org.runerealm.content.skills.fletching.scripts

import jakarta.inject.Inject
import kotlin.math.min
import org.runerealm.api.config.refs.stats
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.player.stat.fletchingLvl
import org.runerealm.api.script.onOpHeldU
import org.runerealm.api.script.onPlayerQueueWithArgs
import org.runerealm.api.stats.xpmod.XpModifiers
import org.runerealm.content.skills.fletching.configs.FletchRecipe
import org.runerealm.content.skills.fletching.configs.FletchingQueues
import org.runerealm.content.skills.fletching.configs.FletchingRecipes
import org.runerealm.content.skills.fletching.configs.fletching_objs
import org.runerealm.game.type.obj.ObjTypeList
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

/**
 * Fletching, per the OSRS Wiki ([Fletching](https://oldschool.runescape.wiki/w/Fletching)):
 * - A knife on logs offers arrow shafts, javelin shafts, unstrung bows and crossbow stocks.
 * - Bow strings on unstrung bows, and crossbow strings on unstrung crossbows.
 * - Limbs on a crossbow stock (with a hammer) to make an unstrung crossbow.
 * - Feathers on arrow shafts, arrowtips on headless arrows (15 per action).
 * - Feathers on unfinished bolts, gem bolt tips on bolts and darts (10 per action).
 * - Javelin heads on javelin shafts (15 per action).
 * - A chisel on cut gems and oyster pearls to make bolt tips.
 */
class Fletching
@Inject
constructor(private val objTypes: ObjTypeList, private val xpMods: XpModifiers) : PluginScript() {
    override fun ScriptContext.startup() {
        for ((log, options) in FletchingRecipes.knifeOnLogs) {
            onOpHeldU(fletching_objs.knife, log) { start(options, KNIFE_TITLE) }
        }
        for (combo in FletchingRecipes.combos) {
            onOpHeldU(combo.first, combo.second) { start(listOf(combo.recipe), MAKE_TITLE) }
        }
        onPlayerQueueWithArgs<FletchTask>(FletchingQueues.fletch) { fletch(it.args) }
    }

    private suspend fun ProtectedAccess.start(options: List<FletchRecipe>, title: String) {
        val actions = options.map { actionsAvailable(it) }
        val skipInterface = options.size == 1 && actions.single() <= 1
        val (recipe, quantity) =
            if (skipInterface) {
                options.single() to 1
            } else {
                val max = actions.max()
                val selection =
                    skillMulti(
                        title = title,
                        choices = options.map { it.product },
                        maxQuantity = max,
                    )
                val selected = options.first { it.product.id == selection.obj.id }
                // The skillmulti interface caps quantities at 28; "All" means all of them.
                val quantity =
                    if (selection.quantity >= SKILL_MULTI_CAP) Int.MAX_VALUE else selection.quantity
                selected to quantity
            }

        if (!canFletch(recipe)) {
            return
        }

        val task = FletchTask(recipe, remaining = quantity)
        if (skipInterface) {
            fletch(task)
        } else {
            weakQueue(FletchingQueues.fletch, FIRST_ACTION_DELAY, task)
        }
    }

    /** The number of actions the carried materials allow for [recipe]. */
    private fun ProtectedAccess.actionsAvailable(recipe: FletchRecipe): Int {
        val units = unitsAvailable(recipe)
        return (units + recipe.batch - 1) / recipe.batch
    }

    private fun ProtectedAccess.unitsAvailable(recipe: FletchRecipe): Int =
        recipe.inputs.minOf { invTotal(inv, it.obj) / it.count }

    private fun ProtectedAccess.canFletch(recipe: FletchRecipe): Boolean {
        if (player.fletchingLvl < recipe.level) {
            val name = objTypes[recipe.product].name.lowercase()
            mes("You need a Fletching level of ${recipe.level} to make ${article(name)}$name.")
            return false
        }
        val missingTool = recipe.tools.firstOrNull { it !in inv }
        if (missingTool != null) {
            val name = objTypes[missingTool].name.lowercase()
            mes("You need ${article(name)}$name to do that.")
            return false
        }
        return unitsAvailable(recipe) > 0
    }

    private fun ProtectedAccess.fletch(task: FletchTask) {
        val recipe = task.recipe
        if (!canFletch(recipe)) {
            return
        }
        val units = min(recipe.batch, unitsAvailable(recipe))
        for (input in recipe.inputs) {
            invDel(inv, input.obj, count = input.count * units)
        }
        val added = invAdd(inv, recipe.product, count = recipe.productCount * units)
        if (added.failure) {
            for (input in recipe.inputs) {
                invAdd(inv, input.obj, count = input.count * units)
            }
            mes("You don't have enough inventory space.")
            return
        }
        recipe.anim?.let(::anim)
        statAdvance(stats.fletching, recipe.xp * units * xpMods.get(player, stats.fletching))
        recipe.message?.let { spam(it.replace("{count}", units.toString())) }

        val next = task.copy(remaining = task.remaining - 1)
        if (next.remaining <= 0 || unitsAvailable(recipe) <= 0) {
            return
        }
        weakQueue(FletchingQueues.fletch, recipe.ticks + 1, next)
    }

    private fun article(name: String): String =
        if (name.endsWith("s")) "" else if (name.first() in "aeiou") "an " else "a "

    data class FletchTask(val recipe: FletchRecipe, val remaining: Int)

    private companion object {
        const val KNIFE_TITLE = "What would you like to make?"
        const val MAKE_TITLE = "How many would you like to make?"

        const val SKILL_MULTI_CAP = 28

        /**
         * Weak queues are decremented on the cycle they are queued, so a delay of `n` executes `n -
         * 1` cycles later: the first action is performed on the cycle after the selection.
         */
        const val FIRST_ACTION_DELAY = 2
    }
}
