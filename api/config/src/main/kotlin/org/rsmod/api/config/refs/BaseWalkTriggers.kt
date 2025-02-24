@file:Suppress("SpellCheckingInspection", "unused")

package org.rsmod.api.config.refs

import org.rsmod.api.type.builders.walktrig.WalkTriggerBuilder

typealias walktriggers = BaseWalkTriggers

object BaseWalkTriggers : WalkTriggerBuilder() {
    val frozen = build("frozen")
    val pvp_frozen = build("pvp_frozen")
    val stunned = build("stunned")
}
