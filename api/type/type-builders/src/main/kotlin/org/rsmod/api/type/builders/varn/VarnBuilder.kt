package org.rsmod.api.type.builders.varn

import org.rsmod.api.type.builders.HashTypeBuilder
import org.rsmod.game.type.varn.UnpackedVarnType
import org.rsmod.game.type.varn.VarnTypeBuilder

public abstract class VarnBuilder : HashTypeBuilder<VarnTypeBuilder, UnpackedVarnType>() {
    override fun build(internal: String, init: VarnTypeBuilder.() -> Unit) {
        val type = VarnTypeBuilder(internal).apply(init).build(id = -1)
        cache += type
    }
}
