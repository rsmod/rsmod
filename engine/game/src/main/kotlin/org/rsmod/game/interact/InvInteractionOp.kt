package org.rsmod.game.interact

public enum class InvInteractionOp(public val slot: Int) {
    Op1(1),
    /** Commonly the `Wear` or `Wield` op. */
    Op2(2),
    Op3(3),
    Op4(4),
    /** Commonly the `Drop` or `Destroy` op. */
    Op5(5),
    Op6(6),
    Op7(7),
}
