package org.rsmod.api.player.events.click

import org.rsmod.events.KeyedEvent

public sealed class ClickEvent(override val id: Long) : KeyedEvent

public sealed class ClickDefaultEvent : ClickEvent(id = -1) {
    public companion object {
        public const val ID: Int = -1
    }
}
