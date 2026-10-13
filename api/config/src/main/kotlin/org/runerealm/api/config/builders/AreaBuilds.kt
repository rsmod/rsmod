package org.runerealm.api.config.builders

import org.runerealm.api.type.builders.area.AreaBuilder

internal object AreaBuilds : AreaBuilder() {
    init {
        build("lumbridge")
        build("singles_plus")
        build("multiway")
    }
}
