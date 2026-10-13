package org.runerealm.content.interfaces.gameframe.config

import org.runerealm.api.type.refs.queue.QueueReferences

typealias gameframe_queues = GameframeQueues

object GameframeQueues : QueueReferences() {
    val client_mode = find("client_mode")
}
