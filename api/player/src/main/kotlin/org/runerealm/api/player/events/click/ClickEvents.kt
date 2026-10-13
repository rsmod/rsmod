package org.runerealm.api.player.events.click

import org.runerealm.events.KeyedEvent

public sealed class ClickEvent(override val id: Long) : KeyedEvent

public sealed class ClickDefaultEvent : ClickEvent(id = -1) {
    public companion object {
        public const val ID: Int = -1
    }
}
