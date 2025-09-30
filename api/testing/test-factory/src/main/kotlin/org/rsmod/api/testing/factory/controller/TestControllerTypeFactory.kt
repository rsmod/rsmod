package org.rsmod.api.testing.factory.controller

import org.rsmod.game.type.controller.ControllerType
import org.rsmod.game.type.controller.ControllerTypeBuilder

public class TestControllerTypeFactory {
    public fun create(id: Int = 0, init: ControllerTypeBuilder.() -> Unit = {}): ControllerType {
        val builder = ControllerTypeBuilder().apply { internalName = "test_controller_type" }
        return builder.apply(init).build(id)
    }
}
