package org.runerealm.api.type.builders.varn

import org.runerealm.api.type.builders.HashTypeBuilder
import org.runerealm.game.type.varn.UnpackedVarnType
import org.runerealm.game.type.varn.VarnTypeBuilder

public abstract class VarnBuilder : HashTypeBuilder<VarnTypeBuilder, UnpackedVarnType>() {
    override fun build(internal: String, init: VarnTypeBuilder.() -> Unit) {
        val type = VarnTypeBuilder(internal).apply(init).build(id = -1)
        cache += type
    }
}
