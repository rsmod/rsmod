package org.runerealm.content.interfaces.prayer.tab.util

import org.runerealm.api.config.constants
import org.runerealm.api.config.refs.queues
import org.runerealm.api.config.refs.timers
import org.runerealm.api.config.refs.varbits
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.player.vars.intVarBit
import org.runerealm.content.interfaces.prayer.tab.Prayer
import org.runerealm.game.entity.Player

internal var Player.drainCounter by intVarBit(varbits.prayer_drain_counter)

internal const val PROTECTION_PRAYERS_DISABLED_MESSAGE =
    "You cannot use protection prayers at the moment."

internal fun ProtectedAccess.enablePrayerStatRegen(prayer: Prayer) {
    when {
        prayer.enabled.isType(varbits.rapid_restore) -> {
            softTimer(timers.rapidrestore_regen, 100)
        }

        prayer.enabled.isType(varbits.rapid_heal) -> {
            softTimer(timers.health_regen, constants.health_regen_interval / 2)
        }

        prayer.enabled.isType(varbits.preserve) -> {
            clearQueue(queues.preserve_activation)
            longQueueDiscard(queues.preserve_activation, 25)
        }
    }
}

internal fun ProtectedAccess.disablePrayerStatRegen(prayer: Prayer) {
    when {
        prayer.enabled.isType(varbits.rapid_restore) -> {
            clearSoftTimer(timers.rapidrestore_regen)
        }

        prayer.enabled.isType(varbits.rapid_heal) -> {
            softTimer(timers.health_regen, constants.health_regen_interval)
        }

        prayer.enabled.isType(varbits.preserve) -> {
            clearQueue(queues.preserve_activation)
            softTimer(timers.stat_boost_restore, constants.stat_boost_restore_interval)
        }
    }
}

internal fun ProtectedAccess.enablePrayerDrain() {
    softTimer(timers.prayer_drain, 1)
}

internal fun ProtectedAccess.disablePrayerDrain() {
    player.drainCounter = 0
    clearSoftTimer(timers.prayer_drain)
}
