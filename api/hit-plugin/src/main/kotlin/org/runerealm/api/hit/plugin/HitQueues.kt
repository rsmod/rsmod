package org.runerealm.api.hit.plugin

import org.runerealm.api.type.refs.queue.QueueReferences
import org.runerealm.game.type.queue.QueueType

public typealias hit_queues = HitQueues

public object HitQueues : QueueReferences() {
    public val standard: QueueType = find("hit")
    public val impact: QueueType = find("impact_hit")
}
