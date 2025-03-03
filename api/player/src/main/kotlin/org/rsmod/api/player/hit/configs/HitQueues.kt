package org.rsmod.api.player.hit.configs

import org.rsmod.api.type.refs.queue.QueueReferences

internal typealias hit_queues = HitQueues

internal object HitQueues : QueueReferences() {
    val standard = find("hit")
    val impact = find("impact_hit")
}
