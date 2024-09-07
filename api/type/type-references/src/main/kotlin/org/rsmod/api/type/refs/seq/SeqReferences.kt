package org.rsmod.api.type.refs.seq

import org.rsmod.api.type.refs.HashTypeReferences
import org.rsmod.game.type.seq.HashedSeqType
import org.rsmod.game.type.seq.SeqType

public abstract class SeqReferences : HashTypeReferences<SeqType>(SeqType::class.java) {
    override fun find(hash: Long): SeqType {
        val type = HashedSeqType(hash)
        cache += type
        return type
    }
}
