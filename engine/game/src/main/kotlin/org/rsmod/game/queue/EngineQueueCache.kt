package org.rsmod.game.queue

import it.unimi.dsi.fastutil.ints.IntOpenHashSet
import it.unimi.dsi.fastutil.longs.LongOpenHashSet

/**
 * Caches "default" and "labelled" script bindings for [EngineQueueType]s.
 *
 * Engine queues are commonly checked and invoked during each game cycle, especially when there is
 * player activity (e.g., movement through the map). Without this cache, events like zone entry and
 * exit would require repeated lookups to determine whether the associated engine queue scripts
 * should be added to a player's engine queue list.
 *
 * While not prohibitively expensive, this overhead is avoidable. This cache provides a fast and
 * simple solution to reduce lookup costs.
 */
public class EngineQueueCache {
    private val labelled = LongOpenHashSet()
    private val defaults = IntOpenHashSet()

    public fun addLabelled(type: EngineQueueType, label: Int) {
        val packed = (type.id.toLong() shl 32) or label.toLong()
        labelled.add(packed)
    }

    private fun hasScript(type: Int, label: Int): Boolean {
        val packed = (type.toLong() shl 32) or label.toLong()
        return labelled.contains(packed)
    }

    public fun hasScript(type: EngineQueueType, label: Int): Boolean {
        return hasScript(type.id, label)
    }

    public fun hasLabelScript(queue: EngineQueueList.Queue): Boolean {
        return hasScript(queue.type, queue.label)
    }

    public fun addDefault(type: EngineQueueType) {
        defaults.add(type.id)
    }

    private fun hasScript(type: Int): Boolean {
        return defaults.contains(type)
    }

    public fun hasScript(type: EngineQueueType): Boolean {
        return hasScript(type.id)
    }

    public fun hasDefaultScript(queue: EngineQueueList.Queue): Boolean {
        return defaults.contains(queue.type)
    }
}
