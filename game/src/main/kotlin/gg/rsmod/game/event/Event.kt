package gg.rsmod.game.event

interface Event

class EventAction<T : Event>(
    val where: (T).() -> Boolean,
    val then: (T).() -> Unit
)
