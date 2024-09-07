package org.rsmod.game.type.mesanim

import org.rsmod.game.type.seq.SeqType

@DslMarker private annotation class MesAnimBuilderDsl

@MesAnimBuilderDsl
public class MesAnimTypeBuilder(public var internal: String? = null) {
    public var len1: SeqType? = null
    public var len2: SeqType? = null
    public var len3: SeqType? = null
    public var len4: SeqType? = null

    public fun build(id: Int): MesAnimType {
        val internal = checkNotNull(internal) { "`internal` must be set. (id=$id)" }
        val len1 = checkNotNull(len1) { "`len1` must be set. (id=$id)" }
        val len2 = checkNotNull(len2) { "`len2` must be set. (id=$id)" }
        val len3 = checkNotNull(len3) { "`len3` must be set. (id=$id)" }
        val len4 = checkNotNull(len4) { "`len4` must be set. (id=$id)" }
        return MesAnimType(len1, len2, len3, len4, id, internal)
    }
}
