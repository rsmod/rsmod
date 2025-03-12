package org.rsmod.api.npc.events.interact

import org.rsmod.api.npc.access.StandardNpcAccess
import org.rsmod.events.SuspendEvent

public sealed class ApEvent(override val id: Long) : SuspendEvent<StandardNpcAccess>

public sealed class OpEvent(override val id: Long) : SuspendEvent<StandardNpcAccess>

// While default events may logically fit better as `UnboundEvent`s, (as they do not contain a key)
// there is no real limit on unbound events. We have no way of guaranteeing the lookup speed of
// these frequently-called and searched events. So instead we store them as `KeyedEvent`s because we
// know there are restrictions on how many of these events there can ever be.
public sealed class ApDefaultEvent : ApEvent(id = -1) {
    public companion object {
        public const val ID: Int = -1
    }
}

public sealed class OpDefaultEvent : OpEvent(id = -1) {
    public companion object {
        public const val ID: Int = -1
    }
}
