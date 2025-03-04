package org.rsmod.api.npc.hit.processor

import jakarta.inject.Inject
import org.rsmod.api.config.refs.params
import org.rsmod.api.npc.access.StandardNpcAccess
import org.rsmod.api.npc.headbar.InternalNpcHeadbars
import org.rsmod.events.EventBus
import org.rsmod.game.headbar.Headbar
import org.rsmod.game.hit.Hit
import org.rsmod.game.type.headbar.HeadbarType

public class StandardNpcHitProcessor @Inject constructor(private val eventBus: EventBus) :
    QueuedNpcHitProcessor {
    override fun StandardNpcAccess.process(hit: Hit) {
        // TODO: Check a "min_health" varn that won't allow target to fall below certain threshold.
        // TODO: Show ironman_blocked hitmark if source is an ironman and target has been damaged
        //  by other sources.
        val mutableHit = hit // TODO: Turn to var and cap damage to npc current hp.
        takeHit(mutableHit)
    }

    private fun StandardNpcAccess.takeHit(hit: Hit) {
        // TODO: Process recoils, retribution?, hero points, etc.
        // TODO: Reduce target health.
        // TODO: onNpcHit script
        npc.showHitmark(hit.hitmark)

        val visHeadbar = npc.visHeadbar(params.headbar)
        val headbar = hit.createHeadbar(5, 10, visHeadbar)
        npc.showHeadbar(headbar)
    }

    private fun Hit.createHeadbar(currHp: Int, maxHp: Int, headbar: HeadbarType): Headbar {
        return when {
            isFromNpc ->
                InternalNpcHeadbars.createNpcSource(
                    sourceSlot = hitmark.npcSlot,
                    currHp = currHp,
                    maxHp = maxHp,
                    headbar = headbar,
                    clientDelay = hitmark.delay,
                )

            isFromPlayer ->
                InternalNpcHeadbars.createPlayerSource(
                    sourceSlot = hitmark.playerSlot,
                    currHp = currHp,
                    maxHp = maxHp,
                    headbar = headbar,
                    clientDelay = hitmark.delay,
                    specific = hitmark.isPrivate,
                )

            else ->
                InternalNpcHeadbars.createNoSource(
                    currHp = currHp,
                    maxHp = maxHp,
                    headbar = headbar,
                    clientDelay = hitmark.delay,
                )
        }
    }
}
