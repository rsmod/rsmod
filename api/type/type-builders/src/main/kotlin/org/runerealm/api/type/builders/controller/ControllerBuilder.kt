package org.runerealm.api.type.builders.controller

import org.runerealm.api.type.builders.NameTypeBuilder
import org.runerealm.game.type.controller.ControllerType
import org.runerealm.game.type.controller.ControllerTypeBuilder

public abstract class ControllerBuilder : NameTypeBuilder<ControllerTypeBuilder, ControllerType>() {
    override fun build(internal: String, init: ControllerTypeBuilder.() -> Unit): ControllerType {
        val type = ControllerTypeBuilder(internal).apply(init).build(id = -1)
        cache += type
        return type
    }
}
