package org.rsmod.content.interfaces.prayer.tab.util

import org.rsmod.api.config.constants
import org.rsmod.api.config.refs.queues
import org.rsmod.api.config.refs.timers
import org.rsmod.api.config.refs.varbits
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.content.interfaces.prayer.tab.Prayer

internal fun ProtectedAccess.enablePrayerStatDrain(prayer: Prayer) {
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

internal fun ProtectedAccess.disablePrayerStatDrain(prayer: Prayer) {
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
