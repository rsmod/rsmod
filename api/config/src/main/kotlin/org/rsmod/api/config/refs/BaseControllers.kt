package org.rsmod.api.config.refs

import org.rsmod.api.type.builders.controller.ControllerBuilder
import org.rsmod.game.type.controller.ControllerType

public typealias controllers = BaseControllers

public object BaseControllers : ControllerBuilder() {
    public val woodcutting_tree_duration: ControllerType = build("woodcutting_tree_duration")
}
