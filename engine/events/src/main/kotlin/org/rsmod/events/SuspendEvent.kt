package org.rsmod.events

public interface SuspendEvent<K> {
    /** Internal key identifier - should **never** be used for content purposes. */
    public val id: Long
}
