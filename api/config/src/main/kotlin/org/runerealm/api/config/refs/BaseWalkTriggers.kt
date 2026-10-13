@file:Suppress("SpellCheckingInspection", "unused")

package org.runerealm.api.config.refs

import org.runerealm.api.type.refs.walktrig.WalkTriggerReferences

typealias walktriggers = BaseWalkTriggers

object BaseWalkTriggers : WalkTriggerReferences() {
    val frozen = find("frozen")
    val pvp_frozen = find("pvp_frozen")
    val stunned = find("stunned")
}
