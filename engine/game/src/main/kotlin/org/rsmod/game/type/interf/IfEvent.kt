package org.rsmod.game.type.interf

public enum class IfEvent(public val bitmask: Int) {
    PauseButton(1 shl 0),
    Op1(1 shl 1),
    Op2(1 shl 2),
    Op3(1 shl 3),
    Op4(1 shl 4),
    Op5(1 shl 5),
    Op6(1 shl 6),
    Op7(1 shl 7),
    Op8(1 shl 8),
    Op9(1 shl 9),
    Op10(1 shl 10),
    TgtObj(1 shl 11),
    TgtNpc(1 shl 12),
    TgtLoc(1 shl 13),
    TgtPlayer(1 shl 14),
    TgtInv(1 shl 15),
    TgtCom(1 shl 16),
    // NOTE: Various Depth entries can overlap with one another.
    // @see documentation at the bottom of IfEvent.kt
    Depth1(1 shl 17),
    Depth2(2 shl 17),
    Depth3(3 shl 17),
    Depth4(4 shl 17),
    Depth5(5 shl 17),
    Depth6(6 shl 17),
    Depth7(7 shl 17),
    DragTarget(1 shl 20),
    Target(1 shl 21),
    CrmTarget(1 shl 22),
    Bit23(1 shl 23),
    Bit24(1 shl 24),
    Bit25(1 shl 25),
    Bit26(1 shl 26),
    Bit27(1 shl 27),
    Bit28(1 shl 28),
    Bit29(1 shl 29),
    Bit30(1 shl 30),
    Bit31(1 shl 31),

    /*
     * Some of the Depth entries can in-fact overlap. Below is a list of their occupying bits.
     * ```
     * Depth1 = 0b0000_0000_0000_0000_0010_0000_0000_0000
     * Depth2 = 0b0000_0000_0000_0000_0100_0000_0000_0000
     * Depth3 = 0b0000_0000_0000_0000_0110_0000_0000_0000
     * Depth4 = 0b0000_0000_0000_0000_1000_0000_0000_0000
     * Depth5 = 0b0000_0000_0000_0000_1010_0000_0000_0000
     * Depth6 = 0b0000_0000_0000_0000_1100_0000_0000_0000
     * Depth7 = 0b0000_0000_0000_0000_1110_0000_0000_0000
     * ```
     * Given this table, we can see that the following entries can share bits:
     *
     * Depth3: overlaps Depth1 and Depth2
     * Depth5: overlaps Depth1 and Depth4
     * Depth6: overlaps Depth2 and Depth4
     * Depth7: overlaps Depth1, Depth2, Depth3, Depth4, Depth5, and Depth6
     *
     * The server will rarely use these event masks, but it is important to know this information
     * should it become relevant for your use-case.
     */
}
