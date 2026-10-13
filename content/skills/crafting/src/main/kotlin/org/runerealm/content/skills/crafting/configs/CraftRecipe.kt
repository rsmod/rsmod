package org.runerealm.content.skills.crafting.configs

import org.runerealm.game.type.obj.ObjType
import org.runerealm.game.type.seq.SeqType

/** An obj (and amount) consumed, or returned, by a single [CraftRecipe] action. */
data class Ingredient(val obj: ObjType, val count: Int = 1)

/**
 * A chance to fail an action: the [CraftRecipe.inputs] are still consumed, but [product] is given
 * instead along with [xp]. Success uses the standard skilling roll between [low] and [high].
 */
data class CraftFailure(val low: Int, val high: Int, val product: ObjType, val xp: Double)

/**
 * A Crafting product.
 *
 * @param inputs objs consumed per action.
 * @param byproducts objs given back per action (e.g., the empty bucket from a bucket of sand).
 * @param tools objs that must be carried but are not consumed (e.g., a chisel or a mould).
 * @param usesThread whether the action uses up the needle's thread: one thread is consumed every
 *   [THREAD_USES] items.
 * @param ticks the delay between consecutive actions.
 */
data class CraftRecipe(
    val product: ObjType,
    val inputs: List<Ingredient>,
    val level: Int,
    val xp: Double,
    val productCount: Int = 1,
    val byproducts: List<Ingredient> = emptyList(),
    val tools: List<ObjType> = emptyList(),
    val usesThread: Boolean = false,
    val failure: CraftFailure? = null,
    val anim: SeqType? = null,
    val ticks: Int = 3,
    val message: String? = null,
) {
    companion object {
        const val THREAD_USES: Int = 5
    }
}
