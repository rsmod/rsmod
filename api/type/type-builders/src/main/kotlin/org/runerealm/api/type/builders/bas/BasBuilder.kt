package org.runerealm.api.type.builders.bas

import org.runerealm.api.type.builders.NameTypeBuilder
import org.runerealm.game.type.bas.BasType
import org.runerealm.game.type.bas.BasTypeBuilder
import org.runerealm.game.type.bas.UnpackedBasType

public abstract class BasBuilder : NameTypeBuilder<BasTypeBuilder, BasType>() {
    override fun build(internal: String, init: BasTypeBuilder.() -> Unit): UnpackedBasType {
        val type = BasTypeBuilder(internal).apply(init).build(id = -1)
        cache += type
        return type
    }
}
