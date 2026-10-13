package org.runerealm.api.type.refs.queue

import org.runerealm.api.type.refs.NameTypeReferences
import org.runerealm.game.type.queue.QueueType
import org.runerealm.game.type.queue.QueueTypeBuilder

public abstract class QueueReferences : NameTypeReferences<QueueType>(QueueType::class.java) {
    override fun find(internal: String): QueueType {
        val type = QueueTypeBuilder(internalName = internal).build(id = -1)
        cache += type
        return type
    }
}
