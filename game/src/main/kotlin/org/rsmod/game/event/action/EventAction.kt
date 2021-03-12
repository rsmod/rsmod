package org.rsmod.game.event.action

import org.rsmod.game.event.Event

class EventAction<T : Event>(
    val where: (T).() -> Boolean,
    val then: (T).() -> Unit
)
