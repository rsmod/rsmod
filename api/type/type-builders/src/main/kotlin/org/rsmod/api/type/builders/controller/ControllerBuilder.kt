package org.rsmod.api.type.builders.controller

import org.rsmod.api.type.builders.NameTypeBuilder
import org.rsmod.game.type.controller.ControllerType
import org.rsmod.game.type.controller.ControllerTypeBuilder

public abstract class ControllerBuilder : NameTypeBuilder<ControllerTypeBuilder, ControllerType>() {
    override fun build(internal: String, init: ControllerTypeBuilder.() -> Unit): ControllerType {
        val type = ControllerTypeBuilder(internal).apply(init).build(id = -1)
        cache += type
        return type
    }
}
