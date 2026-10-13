package org.runerealm.content.interfaces.settings.configs

import org.runerealm.api.type.refs.queue.QueueReferences

typealias setting_queues = SettingQueues

object SettingQueues : QueueReferences() {
    val runmode_toggle = find("runmode_toggle")
}
