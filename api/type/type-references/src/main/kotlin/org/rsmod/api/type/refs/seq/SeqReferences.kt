package org.rsmod.api.type.refs.seq

import org.rsmod.api.type.refs.HashTypeReferences
import org.rsmod.game.type.seq.HashedSeqType
import org.rsmod.game.type.seq.SeqType

public abstract class SeqReferences : HashTypeReferences<SeqType>(SeqType::class.java) {
    override fun find(internal: String, hash: Long?): SeqType {
        val type = HashedSeqType(hash, internalName = internal)
        cache += type
        return type
    }
}
