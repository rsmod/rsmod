package org.rsmod.game.type.seq

public enum class PostanimMove(public val id: Int) {
    /** Stall movement. */
    DelayMove(0),
    /** Cancel animation. */
    AbortAnim(1),
    /** Skid while animating. */
    Merge(2),
}
