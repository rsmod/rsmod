package org.rsmod.api.stats.xpmod

import org.rsmod.game.entity.Player
import org.rsmod.game.type.stat.StatType

fun interface XpMod {
    /**
     * Retrieves an experience rate modifier to be added to the base value of `1.0`. The resulting
     * sum can then be used to multiply the skill experience gained. For example, a modifier of +4%
     * experience would be represented by a return value of `0.04`.
     *
     * This is particularly useful for features like skill outfits that grant bonus experience when
     * worn.
     *
     * **Important:** This modifier only affects calculations in places that explicitly call this
     * function and apply it to the experience granted to players.
     */
    fun Player.modifier(stat: StatType): Double
}
