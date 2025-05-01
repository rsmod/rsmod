package org.rsmod.api.config.builders

import org.rsmod.api.type.builders.area.AreaBuilder

object AreaBuilds : AreaBuilder() {
    init {
        build("singles_plus")
        build("multiway")
    }
}
