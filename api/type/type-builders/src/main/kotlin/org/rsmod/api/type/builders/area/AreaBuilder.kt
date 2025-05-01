package org.rsmod.api.type.builders.area

import org.rsmod.api.type.builders.HashTypeBuilder
import org.rsmod.game.type.area.AreaTypeBuilder
import org.rsmod.game.type.area.UnpackedAreaType

public abstract class AreaBuilder : HashTypeBuilder<AreaTypeBuilder, UnpackedAreaType>() {
    override fun build(internal: String, init: AreaTypeBuilder.() -> Unit) {
        val type = AreaTypeBuilder(internal).apply(init).build(id = -1)
        cache += type
    }
}
