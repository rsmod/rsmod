package org.rsmod.game.type.bas

import org.rsmod.game.type.seq.SeqType

public class BasType(
    public val readyAnim: SeqType,
    public val turnAnim: SeqType,
    public val walkAnim: SeqType,
    public val walkAnimBack: SeqType,
    public val walkAnimLeft: SeqType,
    public val walkAnimRight: SeqType,
    public val runAnim: SeqType,
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
            "runAnim=$runAnim, " +
            "walkAnimRight=$walkAnimRight, " +
            "walkAnimLeft=$walkAnimLeft, " +
            "walkAnimBack=$walkAnimBack, " +
            "walkAnim=$walkAnim, " +
            "turnAnim=$turnAnim, " +
            "readyAnim=$readyAnim" +
            ")"
}
