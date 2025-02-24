package org.rsmod.api.config.builders

import org.rsmod.api.type.builders.walktrig.WalkTriggerBuilder
import org.rsmod.game.type.walktrig.WalkTriggerPriority

object WalkTriggerBuilds : WalkTriggerBuilder() {
    init {
        build("frozen") { priority = WalkTriggerPriority.High }
        build("pvp_frozen") { priority = WalkTriggerPriority.High }
        build("stunned") { priority = WalkTriggerPriority.High }
    }
}
