package org.rsmod.game.type.bas

import org.rsmod.game.type.CacheType
import org.rsmod.game.type.seq.SeqType

public sealed class BasType : CacheType()

public data class UnpackedBasType(
    public val readyAnim: SeqType,
    public val turnOnSpot: SeqType,
    public val walkForward: SeqType,
    public val walkBack: SeqType,
    public val walkLeft: SeqType,
    public val walkRight: SeqType,
    public val running: SeqType,
    override var internalId: Int?,
    override var internalName: String?,
) : BasType() {
    override fun toString(): String =
        "UnpackedBasType(" +
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
