@file:Suppress("SpellCheckingInspection", "unused")

package org.rsmod.api.config.refs

import org.rsmod.api.type.refs.timer.TimerReferences

typealias timers = BaseTimers

object BaseTimers : TimerReferences() {
    val toxins = find("toxins")
}
