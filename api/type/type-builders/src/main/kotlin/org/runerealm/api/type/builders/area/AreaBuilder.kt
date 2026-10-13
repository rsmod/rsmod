package org.runerealm.api.type.builders.area

import org.runerealm.api.type.builders.HashTypeBuilder
import org.runerealm.game.type.area.AreaTypeBuilder
import org.runerealm.game.type.area.UnpackedAreaType

public abstract class AreaBuilder : HashTypeBuilder<AreaTypeBuilder, UnpackedAreaType>() {
    override fun build(internal: String, init: AreaTypeBuilder.() -> Unit) {
        val type = AreaTypeBuilder(internal).apply(init).build(id = -1)
        cache += type
    }
}
