package org.rsmod.api.hit.plugin

import org.rsmod.api.type.refs.queue.QueueReferences
import org.rsmod.game.type.queue.QueueType

public typealias hit_queues = HitQueues

public object HitQueues : QueueReferences() {
    public val standard: QueueType = find("hit")
    public val impact: QueueType = find("impact_hit")
}
