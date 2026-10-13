package org.runerealm.content.interfaces.prayer.tab.configs

import org.runerealm.api.type.refs.queue.QueueReferences

internal typealias prayer_queues = PrayerTabQueues

object PrayerTabQueues : QueueReferences() {
    val toggle = find("prayer_toggle")
    val quick_prayer = find("quick_prayer_toggle")
}
