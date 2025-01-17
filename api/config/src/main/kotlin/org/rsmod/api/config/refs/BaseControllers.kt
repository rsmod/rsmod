@file:Suppress("SpellCheckingInspection", "unused")

package org.rsmod.api.config.refs

import org.rsmod.api.type.builders.controller.ControllerBuilder

typealias controllers = BaseControllers

object BaseControllers : ControllerBuilder() {
    val woodcutting_tree_duration = build("woodcutting_tree_duration")
}
