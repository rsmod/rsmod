package org.rsmod.api.type.builders.bas

import org.rsmod.api.type.builders.NameTypeBuilder
import org.rsmod.game.type.bas.BasType
import org.rsmod.game.type.bas.BasTypeBuilder

public abstract class BasBuilder : NameTypeBuilder<BasTypeBuilder, BasType>() {
    override fun build(internal: String, init: BasTypeBuilder.() -> Unit): BasType {
        val type = BasTypeBuilder(internal).apply(init).build(id = -1)
        cache += type
        return type
    }
}
