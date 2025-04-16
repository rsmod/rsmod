package org.rsmod.api.player.events

import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.events.SuspendEvent

public class EngineQueueEvents {
    public class Default<T>(public val args: T, queueType: Int) : SuspendEvent<ProtectedAccess> {
        override val id: Long = queueType.toLong()
    }

    public class Labelled(override val id: Long) : SuspendEvent<ProtectedAccess>
}
