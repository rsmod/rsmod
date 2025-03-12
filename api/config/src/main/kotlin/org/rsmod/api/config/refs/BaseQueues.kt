@file:Suppress("SpellCheckingInspection", "unused")

package org.rsmod.api.config.refs

import org.rsmod.api.type.refs.queue.QueueReferences

typealias queues = BaseQueues

object BaseQueues : QueueReferences() {
    val generic_queue1 = find("generic_queue1")
    val generic_queue2 = find("generic_queue2")
    val generic_queue3 = find("generic_queue3")
    val generic_queue4 = find("generic_queue4")
    val generic_queue5 = find("generic_queue5")
    val generic_queue6 = find("generic_queue6")
    val generic_queue7 = find("generic_queue7")
    val generic_queue8 = find("generic_queue8")
    val generic_queue9 = find("generic_queue9")
    val generic_queue10 = find("generic_queue10")
    val fade_overlay_close = find("fade_overlay_close")
    val death = find("death")
    val com_retaliate = find("com_retaliate")
}
