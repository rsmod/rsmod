package org.runerealm.api.type.script.dsl

import org.runerealm.game.type.mesanim.MesAnimTypeBuilder
import org.runerealm.game.type.mesanim.UnpackedMesAnimType
import org.runerealm.game.type.seq.SeqType

@DslMarker private annotation class MesAnimBuilderDsl

@MesAnimBuilderDsl
public class MesAnimPluginBuilder(public var internal: String? = null) {
    private val backing: MesAnimTypeBuilder = MesAnimTypeBuilder()

    public var len1: SeqType? by backing::len1
    public var len2: SeqType? by backing::len2
    public var len3: SeqType? by backing::len3
    public var len4: SeqType? by backing::len4

    public fun build(id: Int): UnpackedMesAnimType {
        backing.internal = internal
        return backing.build(id)
    }
}
