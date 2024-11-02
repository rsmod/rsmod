package org.rsmod.game.queue

public enum class QueueCategory(public val id: Int) {
    Soft(0),
    Normal(1),
    Weak(2),
    Strong(3),
    LongAccelerate(4),
    LongDiscard(5),
}
