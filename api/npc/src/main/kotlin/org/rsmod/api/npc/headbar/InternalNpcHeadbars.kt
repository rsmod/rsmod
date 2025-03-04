package org.rsmod.api.npc.headbar

import org.rsmod.game.headbar.Headbar
import org.rsmod.game.type.headbar.HeadbarType

internal object InternalNpcHeadbars {
    fun createNpcSource(
        sourceSlot: Int,
        currHp: Int,
        maxHp: Int,
        headbar: HeadbarType,
        clientDelay: Int,
    ): Headbar {
        val fill = calculateFill(headbar.segments, currHp, maxHp)
        return Headbar.fromNpcSource(
            self = headbar.id,
            public = headbar.id,
            startFill = fill,
            endFill = fill,
            startTime = clientDelay,
            endTime = clientDelay,
            slotId = sourceSlot,
        )
    }

    fun createPlayerSource(
        sourceSlot: Int,
        currHp: Int,
        maxHp: Int,
        headbar: HeadbarType,
        clientDelay: Int,
        specific: Boolean,
    ): Headbar {
        val fill = calculateFill(headbar.segments, currHp, maxHp)
        return Headbar.fromPlayerSource(
            self = headbar.id,
            public = if (specific) null else headbar.id,
            startFill = fill,
            endFill = fill,
            startTime = clientDelay,
            endTime = clientDelay,
            slotId = sourceSlot,
        )
    }

    fun createNoSource(currHp: Int, maxHp: Int, headbar: HeadbarType, clientDelay: Int): Headbar {
        val fill = calculateFill(headbar.segments, currHp, maxHp)
        return Headbar.fromNoSource(
            self = headbar.id,
            public = headbar.id,
            startFill = fill,
            endFill = fill,
            startTime = clientDelay,
            endTime = clientDelay,
        )
    }

    private fun calculateFill(segments: Int, currHp: Int, maxHp: Int): Int {
        return (currHp * segments) / maxHp
    }
}
