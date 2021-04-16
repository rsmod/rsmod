package org.rsmod.game.model.ui

import com.google.common.base.MoreObjects

inline class UserInterface(val id: Int) {

    fun child(child: Int): Component {
        return Component(id, child)
    }
}

inline class Component(val packed: Int) {

    val interfaceId: Int
        get() = (packed shr 16) and 0xFFFF

    val child: Int
        get() = packed and 0xFFFF

    constructor(interfaceId: Int, child: Int) : this(
        (interfaceId shl 16) or (child and 0xFFFF)
    )

    operator fun component1(): Int = interfaceId

    operator fun component2(): Int = child

    override fun toString(): String = MoreObjects.toStringHelper(this)
        .add("interfaceId", interfaceId)
        .add("child", child)
        .toString()
}

data class ComponentProperty(
    private val sub: MutableSet<DynamicComponentEvent> = mutableSetOf()
) : Set<DynamicComponentEvent> by sub {

    fun add(event: DynamicComponentEvent): Boolean {
        val range = event.range
        val occupied = sub.any { it.range.within(range) }
        if (occupied) {
            return false
        }
        return sub.add(event)
    }

    fun remove(event: DynamicComponentEvent): Boolean {
        return sub.remove(event)
    }
}

data class DynamicComponentEvent(
    val range: IntRange,
    val packed: Int
)

private fun IntRange.within(other: IntRange): Boolean {
    return first >= other.first && last <= other.last
}
