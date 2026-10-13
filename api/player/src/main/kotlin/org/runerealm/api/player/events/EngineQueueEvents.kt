package org.runerealm.api.player.events

import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.events.SuspendEvent

public class EngineQueueEvents {
    public class Default<T>(public val args: T, queueType: Int) : SuspendEvent<ProtectedAccess> {
        override val id: Long = queueType.toLong()
    }

    public class Labelled(override val id: Long) : SuspendEvent<ProtectedAccess>
}
