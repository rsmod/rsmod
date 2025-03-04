package org.rsmod.api.type.builders.headbar

import org.rsmod.api.type.builders.HashTypeBuilder
import org.rsmod.game.type.headbar.HeadbarTypeBuilder
import org.rsmod.game.type.headbar.UnpackedHeadbarType

public abstract class HeadbarBuilder : HashTypeBuilder<HeadbarTypeBuilder, UnpackedHeadbarType>() {
    override fun build(internal: String, init: HeadbarTypeBuilder.() -> Unit) {
        val type = HeadbarTypeBuilder(internal).apply(init).build(id = -1)
        cache += type
    }
}
