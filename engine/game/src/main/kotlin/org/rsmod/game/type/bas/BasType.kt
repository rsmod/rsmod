package org.rsmod.game.type.bas

import org.rsmod.game.type.seq.SeqType

public class BasType(
    public val readyAnim: SeqType,
    public val turnOnSpot: SeqType,
    public val walkForward: SeqType,
    public val walkBack: SeqType,
    public val walkLeft: SeqType,
    public val walkRight: SeqType,
    public val running: SeqType,
    internal var internalId: Int,
    internal var internalName: String,
) {
    public val id: Int
        get() = internalId

    public val internalNameGet: String
        get() = internalName

    override fun toString(): String =
        "BasType(" +
            "internalName='$internalName', " +
            "internalId=$internalId, " +
            "readyAnim=$readyAnim, " +
            "turnOnSpot=$turnOnSpot, " +
            "walkForward=$walkForward, " +
            "walkBack=$walkBack, " +
            "walkLeft=$walkLeft, " +
            "walkRight=$walkRight, " +
            "running=$running" +
            ")"
}
