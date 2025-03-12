package org.rsmod.api.player.headbar

import org.rsmod.game.headbar.Headbar
import org.rsmod.game.hit.Hitmark
import org.rsmod.game.type.headbar.HeadbarType

internal object InternalPlayerHeadbars {
    fun createFromHitmark(
        hitmark: Hitmark,
        currHp: Int,
        maxHp: Int,
        headbar: HeadbarType,
    ): Headbar {
        return when {
            hitmark.isNpcSource ->
                createNpcSource(
                    sourceSlot = hitmark.npcSlot,
                    currHp = currHp,
                    maxHp = maxHp,
                    headbar = headbar,
                    clientDelay = hitmark.delay,
                )

            hitmark.isPlayerSource ->
                createPlayerSource(
                    sourceSlot = hitmark.playerSlot,
                    currHp = currHp,
                    maxHp = maxHp,
                    headbar = headbar,
                    clientDelay = hitmark.delay,
                    specific = hitmark.isPrivate,
                )

            else ->
                createNoSource(
                    currHp = currHp,
                    maxHp = maxHp,
                    headbar = headbar,
                    clientDelay = hitmark.delay,
                )
        }
    }

    private fun createNpcSource(
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

    private fun createPlayerSource(
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

    private fun createNoSource(
        currHp: Int,
        maxHp: Int,
        headbar: HeadbarType,
        clientDelay: Int,
    ): Headbar {
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
