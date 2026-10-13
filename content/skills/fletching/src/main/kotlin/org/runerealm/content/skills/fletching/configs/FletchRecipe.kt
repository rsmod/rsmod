package org.runerealm.content.skills.fletching.configs

import org.runerealm.game.type.obj.ObjType
import org.runerealm.game.type.seq.SeqType

/** An obj (and amount) consumed by a single unit of a [FletchRecipe]. */
data class Ingredient(val obj: ObjType, val count: Int = 1)

/**
 * A Fletching product.
 *
 * Each action makes up to [batch] "units". A unit consumes every [inputs] entry once, produces
 * [productCount] of [product] and grants [xp] experience. For example, arrows are made in batches
 * of up to 15 (one headless arrow and one arrowtip per arrow, 1.3 xp per bronze arrow), whereas a
 * knife on logs makes a single unit of 15 arrow shafts for 5 xp.
 *
 * @param tools objs that must be carried but are not consumed (e.g., a knife or a hammer).
 * @param ticks the delay between consecutive actions.
 */
data class FletchRecipe(
    val product: ObjType,
    val inputs: List<Ingredient>,
    val level: Int,
    val xp: Double,
    val productCount: Int = 1,
    val batch: Int = 1,
    val tools: List<ObjType> = emptyList(),
    val anim: SeqType? = null,
    val ticks: Int = 3,
    val message: String? = null,
)
