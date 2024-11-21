package org.rsmod.api.config.refs

import org.rsmod.api.type.refs.queue.QueueReferences
import org.rsmod.game.type.queue.QueueType

public typealias queues = BaseQueues

public object BaseQueues : QueueReferences() {
    public val generic_queue1: QueueType = find("generic_queue1")
    public val generic_queue2: QueueType = find("generic_queue2")
    public val generic_queue3: QueueType = find("generic_queue3")
    public val generic_queue4: QueueType = find("generic_queue4")
    public val generic_queue5: QueueType = find("generic_queue5")
    public val generic_queue6: QueueType = find("generic_queue6")
    public val generic_queue7: QueueType = find("generic_queue7")
    public val generic_queue8: QueueType = find("generic_queue8")
    public val generic_queue9: QueueType = find("generic_queue9")
    public val generic_queue10: QueueType = find("generic_queue10")
}
