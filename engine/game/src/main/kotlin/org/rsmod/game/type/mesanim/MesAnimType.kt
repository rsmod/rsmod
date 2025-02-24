package org.rsmod.game.type.mesanim

import org.rsmod.game.type.CacheType
import org.rsmod.game.type.seq.SeqType

public sealed class MesAnimType : CacheType()

public data class UnpackedMesAnimType(
    public val len1: SeqType,
    public val len2: SeqType,
    public val len3: SeqType,
    public val len4: SeqType,
    override var internalId: Int?,
    override var internalName: String?,
) : MesAnimType() {
    override fun toString(): String =
        "UnpackedMesAnimType(" +
            "internalId=$internalId, " +
            "internalName=$internalName, " +
            "len1=$len1, " +
            "len2=$len2, " +
            "len3=$len3, " +
            "len4=$len4" +
            ")"
}
