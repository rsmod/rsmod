package org.runerealm.api.config.builders

import org.runerealm.api.type.builders.walktrig.WalkTriggerBuilder
import org.runerealm.game.type.walktrig.WalkTriggerPriority

internal object WalkTriggerBuilds : WalkTriggerBuilder() {
    init {
        build("frozen") { priority = WalkTriggerPriority.High }
        build("pvp_frozen") { priority = WalkTriggerPriority.High }
        build("stunned") { priority = WalkTriggerPriority.High }
    }
}
