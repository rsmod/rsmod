package org.rsmod.game.type.mesanim

import org.rsmod.game.type.seq.SeqType

public class MesAnimType(
    public val len1: SeqType,
    public val len2: SeqType,
    public val len3: SeqType,
    public val len4: SeqType,
    internal var internalId: Int,
    internal var internalName: String,
) {
    public val id: Int
        get() = internalId

    public val internalNameGet: String
        get() = internalName

    override fun toString(): String =
        "MesAnimType(" +
            "internalId=$internalId, " +
            "internalName=$internalName, " +
            "len1=$len1, " +
            "len2=$len2, " +
            "len3=$len3, " +
            "len4=$len4" +
            ")"
}
