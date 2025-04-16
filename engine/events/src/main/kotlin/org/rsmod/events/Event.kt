package org.rsmod.events

public interface UnboundEvent

public interface KeyedEvent {
    /** Internal key identifier - should **never** be used for content purposes. */
    public val id: Long
}

public interface SuspendEvent<K> {
    /** Internal key identifier - should **never** be used for content purposes. */
    public val id: Long
}
