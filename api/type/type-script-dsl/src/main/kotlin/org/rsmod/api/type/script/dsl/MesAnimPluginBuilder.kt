package org.rsmod.api.type.script.dsl

import org.rsmod.game.type.mesanim.MesAnimType
import org.rsmod.game.type.mesanim.MesAnimTypeBuilder
import org.rsmod.game.type.seq.SeqType

@DslMarker private annotation class MesAnimBuilderDsl

@MesAnimBuilderDsl
public class MesAnimPluginBuilder(public var internal: String? = null) {
    private val backing: MesAnimTypeBuilder = MesAnimTypeBuilder()

    public var len1: SeqType? = null
        set(value) {
            backing.len1 = value
        }

    public var len2: SeqType? = null
        set(value) {
            backing.len2 = value
        }

    public var len3: SeqType? = null
        set(value) {
            backing.len3 = value
        }

    public var len4: SeqType? = null
        set(value) {
            backing.len4 = value
        }

    public fun build(id: Int): MesAnimType {
        backing.internal = internal
        return backing.build(id)
    }
}
