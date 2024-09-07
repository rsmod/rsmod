package org.rsmod.game.type.seq

public enum class PreanimMove(public val id: Int) {
    /** Stall movement. */
    DelayMove(0),
    /** Finish movement, then animate. */
    DelayAnim(1),
    /** Skid while animating. */
    Merge(2),
}
