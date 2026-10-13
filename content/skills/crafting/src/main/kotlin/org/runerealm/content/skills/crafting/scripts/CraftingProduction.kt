package org.runerealm.content.skills.crafting.scripts

import jakarta.inject.Inject
import jakarta.inject.Singleton
import java.util.WeakHashMap
import org.runerealm.api.config.refs.stats
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.player.stat.craftingLvl
import org.runerealm.api.stats.xpmod.XpModifiers
import org.runerealm.content.skills.crafting.configs.CraftRecipe
import org.runerealm.content.skills.crafting.configs.CraftingQueues
import org.runerealm.content.skills.crafting.configs.crafting_objs
import org.runerealm.game.entity.Player
import org.runerealm.game.type.obj.ObjTypeList

/**
 * Shared Make-X production loop for Crafting: offers [CraftRecipe] options through the skillmulti
 * interface, then performs one action every [CraftRecipe.ticks] through a weak queue (so walking
 * away or doing something else stops it).
 */
@Singleton
class CraftingProduction
@Inject
constructor(private val objTypes: ObjTypeList, private val xpMods: XpModifiers) {
    /** Items made with the current spool of thread, per player. */
    private val threadUses = WeakHashMap<Player, Int>()

    suspend fun start(access: ProtectedAccess, options: List<CraftRecipe>, title: String) {
        with(access) {
            val actions = options.map { countActions(it) }
            if (actions.all { it == 0 }) {
                mes("You don't have the materials to make anything.")
                return
            }
            val skipInterface = options.size == 1 && actions.single() <= 1
            if (skipInterface) {
                val recipe = options.single()
                if (canCraft(recipe)) {
                    craftOnce(CraftTask(recipe, remaining = 1))
                }
                return
            }
            val selection =
                skillMulti(
                    title = title,
                    choices = options.map { it.product },
                    maxQuantity = actions.max(),
                )
            val recipe = options.first { it.product.id == selection.obj.id }
            // The skillmulti interface caps quantities at 28; "All" means all of them.
            val quantity =
                if (selection.quantity >= SKILL_MULTI_CAP) Int.MAX_VALUE else selection.quantity
            queueTask(recipe, quantity)
        }
    }

    /** Starts making [quantity] of [recipe] from the next cycle, if the player is able to. */
    fun queue(access: ProtectedAccess, recipe: CraftRecipe, quantity: Int) {
        access.queueTask(recipe, quantity)
    }

    private fun ProtectedAccess.queueTask(recipe: CraftRecipe, quantity: Int) {
        if (!canCraft(recipe)) {
            return
        }
        weakQueue(CraftingQueues.craft, FIRST_ACTION_DELAY, CraftTask(recipe, quantity))
    }

    private fun ProtectedAccess.countActions(recipe: CraftRecipe): Int {
        val actions = recipe.inputs.minOf { invTotal(inv, it.obj) / it.count }
        if (!recipe.usesThread) {
            return actions
        }
        val uses = threadUses[player] ?: 0
        val threadActions = invTotal(inv, crafting_objs.thread) * CraftRecipe.THREAD_USES - uses
        return minOf(actions, threadActions.coerceAtLeast(0))
    }

    private fun ProtectedAccess.canCraft(recipe: CraftRecipe): Boolean {
        if (player.craftingLvl < recipe.level) {
            val name = objTypes[recipe.product].name.lowercase()
            mes("You need a Crafting level of ${recipe.level} to make ${article(name)}$name.")
            return false
        }
        val missingTool = recipe.tools.firstOrNull { it !in inv }
        if (missingTool != null) {
            val name = objTypes[missingTool].name.lowercase()
            mes("You need ${article(name)}$name to make this.")
            return false
        }
        if (recipe.usesThread && crafting_objs.thread !in inv) {
            mes("You need some thread to make anything out of leather.")
            return false
        }
        val missing = recipe.inputs.firstOrNull { invTotal(inv, it.obj) < it.count }
        if (missing != null) {
            val name = objTypes[missing.obj].name.lowercase()
            mes("You don't have enough $name to make this.")
            return false
        }
        return true
    }

    fun craft(access: ProtectedAccess, task: CraftTask) {
        access.craftOnce(task)
    }

    private fun ProtectedAccess.craftOnce(task: CraftTask) {
        val recipe = task.recipe
        if (!canCraft(recipe)) {
            return
        }
        for (input in recipe.inputs) {
            invDel(inv, input.obj, count = input.count)
        }

        val failure = recipe.failure
        val success =
            failure == null ||
                statRandom(stats.crafting, failure.low, failure.high, invisibleBoost = 0)
        val product = if (success) recipe.product else failure!!.product
        val count = if (success) recipe.productCount else 1
        val added = invAdd(inv, product, count = count)
        if (added.failure) {
            for (input in recipe.inputs) {
                invAdd(inv, input.obj, count = input.count)
            }
            mes("You don't have enough inventory space.")
            return
        }
        for (byproduct in recipe.byproducts) {
            invAdd(inv, byproduct.obj, count = byproduct.count, strict = false)
        }
        if (recipe.usesThread) {
            useThread()
        }

        recipe.anim?.let(::anim)
        val xp = if (success) recipe.xp else failure!!.xp
        if (xp > 0.0) {
            statAdvance(stats.crafting, xp * xpMods.get(player, stats.crafting))
        }
        if (success) {
            spam(recipe.message ?: defaultMessage(recipe))
        } else {
            val gem = objTypes[recipe.product].name.lowercase()
            spam("You mis-hit the chisel and smash the $gem to pieces!")
        }

        val next = task.copy(remaining = task.remaining - 1)
        if (next.remaining <= 0 || countActions(recipe) <= 0) {
            return
        }
        weakQueue(CraftingQueues.craft, recipe.ticks + 1, next)
    }

    private fun ProtectedAccess.useThread() {
        val uses = (threadUses[player] ?: 0) + 1
        if (uses >= CraftRecipe.THREAD_USES) {
            invDel(inv, crafting_objs.thread, count = 1)
            threadUses.remove(player)
            mes("You use up one of your reels of thread.")
        } else {
            threadUses[player] = uses
        }
    }

    private fun defaultMessage(recipe: CraftRecipe): String {
        val name = objTypes[recipe.product].name.lowercase()
        return if (recipe.productCount > 1) {
            "You make ${recipe.productCount} $name."
        } else {
            "You make ${article(name)}$name."
        }
    }

    private fun article(name: String): String =
        if (name.endsWith("s")) "" else if (name.first() in "aeiou") "an " else "a "

    data class CraftTask(val recipe: CraftRecipe, val remaining: Int)

    private companion object {
        const val SKILL_MULTI_CAP = 28

        /**
         * Weak queues are decremented on the cycle they are queued, so a delay of `n` executes `n -
         * 1` cycles later: the first action is performed on the cycle after the selection.
         */
        const val FIRST_ACTION_DELAY = 2
    }
}
