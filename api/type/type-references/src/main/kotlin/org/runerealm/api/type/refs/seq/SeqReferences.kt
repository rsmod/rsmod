package org.runerealm.api.type.refs.seq

import org.runerealm.api.type.refs.HashTypeReferences
import org.runerealm.game.type.seq.HashedSeqType
import org.runerealm.game.type.seq.SeqType

public abstract class SeqReferences : HashTypeReferences<SeqType>(SeqType::class.java) {
    override fun find(internal: String, hash: Long?): SeqType {
        val type = HashedSeqType(hash, internal)
        cache += type
        return type
    }
}
