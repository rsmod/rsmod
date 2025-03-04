package org.rsmod.api.config.builders

import org.rsmod.api.config.refs.varns
import org.rsmod.api.type.builders.varnbit.VarnBitBuilder

object VarnBitBuilds : VarnBitBuilder() {
    init {
        build("respawn_pending") {
            baseVar = varns.generic_state_2
            startBit = 0
            endBit = 0
        }
    }
}
