package org.rsmod.api.stats.levelmod

import org.rsmod.game.entity.Player
import org.rsmod.game.type.stat.StatType

abstract class InvisibleLevelMod(internal val stat: StatType) {
    /**
     * Calculates the invisible level boost for the [stat] skill of the [Player] in scope. The boost
     * is a positive integer added to the player's visual level to influence skill success rate
     * rolls.
     */
    abstract fun Player.calculateBoost(): Int
}
