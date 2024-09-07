package org.rsmod.api.type.builders.loc

import org.rsmod.api.type.builders.HashTypeBuilder
import org.rsmod.game.type.loc.LocTypeBuilder
import org.rsmod.game.type.loc.UnpackedLocType

public abstract class LocBuilder : HashTypeBuilder<LocTypeBuilder, UnpackedLocType>() {
    override fun build(internal: String, init: LocTypeBuilder.() -> Unit) {
        val type = LocTypeBuilder(internal).apply(init).build(id = -1)
        cache += type
    }
}
