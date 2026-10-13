package org.runerealm.api.type.builders.stat

import org.runerealm.api.type.builders.HashTypeBuilder
import org.runerealm.game.type.stat.StatTypeBuilder
import org.runerealm.game.type.stat.UnpackedStatType

public abstract class StatBuilder : HashTypeBuilder<StatTypeBuilder, UnpackedStatType>() {
    override fun build(internal: String, init: StatTypeBuilder.() -> Unit) {
        val type = StatTypeBuilder(internal).apply(init).build(id = -1)
        cache += type
    }
}
