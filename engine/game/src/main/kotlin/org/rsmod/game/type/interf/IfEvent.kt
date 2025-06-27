package org.rsmod.game.type.interf

public enum class IfEvent(public val bitmask: Long) {
    PauseButton(1L shl 0),
    DeprecatedOp1(1L shl 1),
    DeprecatedOp2(1L shl 2),
    DeprecatedOp3(1L shl 3),
    DeprecatedOp4(1L shl 4),
    DeprecatedOp5(1L shl 5),
    DeprecatedOp6(1L shl 6),
    DeprecatedOp7(1L shl 7),
    DeprecatedOp8(1L shl 8),
    DeprecatedOp9(1L shl 9),
    DeprecatedOp10(1L shl 10),
    TgtObj(1L shl 11),
    TgtNpc(1L shl 12),
    TgtLoc(1L shl 13),
    TgtPlayer(1L shl 14),
    TgtInv(1L shl 15),
    TgtCom(1L shl 16),
    // NOTE: Various Depth entries can overlap with one another.
    // @see documentation at the bottom of IfEvent.kt
    Depth1(1L shl 17),
    Depth2(2L shl 17),
    Depth3(3L shl 17),
    Depth4(4L shl 17),
    Depth5(5L shl 17),
    Depth6(6L shl 17),
    Depth7(7L shl 17),
    DragTarget(1L shl 20),
    Target(1L shl 21),
    CrmTarget(1L shl 22),
    Op1(1L shl 32),
    Op2(1L shl 33),
    Op3(1L shl 34),
    Op4(1L shl 35),
    Op5(1L shl 36),
    Op6(1L shl 37),
    Op7(1L shl 38),
    Op8(1L shl 39),
    Op9(1L shl 40),
    Op10(1L shl 41),
    Op11(1L shl 42),
    Op12(1L shl 43),
    Op13(1L shl 44),
    Op14(1L shl 45),
    Op15(1L shl 46),
    Op16(1L shl 47),
    Op17(1L shl 48),
    Op18(1L shl 49),
    Op19(1L shl 50),
    Op20(1L shl 51),
    Op21(1L shl 52),
    Op22(1L shl 53),
    Op23(1L shl 54),
    Op24(1L shl 55),
    Op25(1L shl 56),
    Op26(1L shl 57),
    Op27(1L shl 58),
    Op28(1L shl 59),
    Op29(1L shl 60),
    Op30(1L shl 61),
    Op31(1L shl 62),
    Op32(1L shl 63),
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
