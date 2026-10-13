package org.runerealm.api.npc.hit.configs

import org.runerealm.api.type.refs.queue.QueueReferences

internal typealias hit_queues = HitQueues

internal object HitQueues : QueueReferences() {
    val standard = find("hit")
}
