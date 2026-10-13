@file:Suppress("SpellCheckingInspection", "unused")

package org.runerealm.api.config.refs

import org.runerealm.api.type.refs.timer.TimerReferences

typealias timers = BaseTimers

object BaseTimers : TimerReferences() {
    val toxins = find("toxins")
    val stat_regen = find("stat_regen")
    val stat_boost_restore = find("stat_boost_restore")
    val health_regen = find("health_regen")
    val rapidrestore_regen = find("rapidrestore_regen")
    val spec_regen = find("spec_regen")
    val prayer_drain = find("prayer_drain")
    val pk_skull = find("pk_skull")
    val freeze_thaw = find("freeze_thaw")
}
