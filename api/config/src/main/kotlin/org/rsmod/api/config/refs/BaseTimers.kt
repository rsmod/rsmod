@file:Suppress("SpellCheckingInspection", "unused")

package org.rsmod.api.config.refs

import org.rsmod.api.type.refs.timer.TimerReferences

typealias timers = BaseTimers

object BaseTimers : TimerReferences() {
    val toxins = find("toxins")
    val stat_regen = find("stat_regen")
    val stat_boost_restore = find("stat_boost_restore")
    val health_regen = find("health_regen")
    val rapidrestore_regen = find("rapidrestore_regen")
}
