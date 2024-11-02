package org.rsmod.api.type.refs.queue

import org.rsmod.api.type.refs.NameTypeReferences
import org.rsmod.game.type.queue.QueueType
import org.rsmod.game.type.queue.QueueTypeBuilder

public abstract class QueueReferences : NameTypeReferences<QueueType>(QueueType::class.java) {
    override fun find(internal: String): QueueType {
        val type = QueueTypeBuilder(internalName = internal).build(id = -1)
        cache += type
        return type
    }
}
