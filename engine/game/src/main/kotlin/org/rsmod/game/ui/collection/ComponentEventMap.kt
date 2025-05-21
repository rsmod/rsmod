package org.rsmod.game.ui.collection

import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import it.unimi.dsi.fastutil.longs.LongArrayList
import it.unimi.dsi.fastutil.longs.LongList
import org.rsmod.game.type.comp.ComponentType
import org.rsmod.game.ui.UserInterface

public class ComponentEventMap(
    private val interfaces: Int2ObjectMap<LongList> = Int2ObjectOpenHashMap()
) {
    public operator fun get(type: ComponentType, slot: Int): Int {
        val eventList = interfaces[type.interfaceId]
        if (eventList == interfaces.defaultReturnValue()) {
            return 0
        }
        var events = 0
        for (i in 0 until eventList.size) {
            val event = Event(eventList.getLong(i))
            if (event.component == type.component && slot >= event.start && slot <= event.end) {
                events = event.events
            }
        }
        return events
    }

    public fun add(type: ComponentType, range: IntRange, events: Int) {
        val eventList = interfaces.computeIfAbsent(type.interfaceId) { LongArrayList() }
        val event = Event(type.component, range.first, range.last, events)
        eventList.add(event.packed)
    }

    public fun clear(interf: UserInterface) {
        interfaces.remove(interf.id)
    }

    @JvmInline
    private value class Event(val packed: Long) {
        val component: Int
            get() = ((packed shr COMPONENT_BIT_OFFSET) and COMPONENT_BIT_MASK).toInt()

        val start: Int
            get() = ((packed shr START_BIT_OFFSET) and START_BIT_MASK).toInt()

        val end: Int
            get() = ((packed shr END_BIT_OFFSET) and END_BIT_MASK).toInt()

        val events: Int
            get() = ((packed shr EVENTS_BIT_OFFSET) and EVENTS_BIT_MASK).toInt()

        constructor(
            component: Int,
            start: Int,
            end: Int,
            events: Int,
        ) : this(pack(component, start, end, events))

        override fun toString(): String {
            return "Event(component=$component, range=$start..$end, events=$events)"
        }

        companion object {
            const val COMPONENT_BIT_COUNT = 13
            const val START_BIT_COUNT = 14
            const val END_BIT_COUNT = 14
            const val EVENTS_BIT_COUNT = 23

            const val COMPONENT_BIT_OFFSET = 0
            const val START_BIT_OFFSET = COMPONENT_BIT_OFFSET + COMPONENT_BIT_COUNT
            const val END_BIT_OFFSET = START_BIT_OFFSET + START_BIT_COUNT
            const val EVENTS_BIT_OFFSET = END_BIT_OFFSET + END_BIT_COUNT

            const val COMPONENT_BIT_MASK = (1L shl COMPONENT_BIT_COUNT) - 1
            const val START_BIT_MASK = (1L shl START_BIT_COUNT) - 1
            const val END_BIT_MASK = (1L shl END_BIT_COUNT) - 1
            const val EVENTS_BIT_MASK = (1L shl EVENTS_BIT_COUNT) - 1

            private fun pack(component: Int, start: Int, end: Int, events: Int): Long {
                val clampedStart = if (start == -1) 0 else start
                val clampedEnd = if (end == -1) END_BIT_MASK.toInt() else end
                require(component in 0..COMPONENT_BIT_MASK) {
                    "`component` must be in range [0..$COMPONENT_BIT_MASK]"
                }
                require(clampedStart in 0..START_BIT_MASK) {
                    "`start` must be in range [-1..$START_BIT_MASK]"
                }
                require(clampedEnd in 0..END_BIT_MASK) {
                    "`end` must be in range [-1..$END_BIT_MASK]"
                }
                require(events in 0..EVENTS_BIT_MASK) {
                    "`events` must be in range [0..$EVENTS_BIT_MASK]"
                }
                return ((component.toLong() and COMPONENT_BIT_MASK) shl COMPONENT_BIT_OFFSET) or
                    ((clampedStart.toLong() and START_BIT_MASK) shl START_BIT_OFFSET) or
                    ((clampedEnd.toLong() and END_BIT_MASK) shl END_BIT_OFFSET) or
                    ((events.toLong() and EVENTS_BIT_MASK) shl EVENTS_BIT_OFFSET)
            }
        }
    }
}
