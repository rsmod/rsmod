package gg.rsmod.game.event

class EventAction<T : Event>(
    val where: (T).() -> Boolean,
    val then: (T).() -> Unit
)
