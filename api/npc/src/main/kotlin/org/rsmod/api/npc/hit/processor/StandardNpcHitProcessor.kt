package org.rsmod.api.npc.hit.processor

import jakarta.inject.Inject
import org.rsmod.api.config.refs.hitmarks
import org.rsmod.api.config.refs.params
import org.rsmod.api.config.refs.queues
import org.rsmod.api.npc.access.StandardNpcAccess
import org.rsmod.api.npc.headbar.InternalNpcHeadbars
import org.rsmod.events.EventBus
import org.rsmod.game.headbar.Headbar
import org.rsmod.game.hit.Hit
import org.rsmod.game.type.headbar.HeadbarType

public class StandardNpcHitProcessor @Inject constructor(private val eventBus: EventBus) :
    QueuedNpcHitProcessor {
    override fun StandardNpcAccess.process(hit: Hit) {
        // TODO(combat): Show ironman_blocked hitmark if source is an ironman and target has been
        // damaged by other sources.

        var changedDamage: Int? = null
        if (hit.damage > npc.hitpoints) {
            changedDamage = npc.hitpoints
        }

        // TODO(combat): Check a "min_health" varn that won't allow target to fall below certain
        // threshold.

        if (changedDamage == 0) {
            val modifiedHitmark =
                hit.hitmark.copy(
                    self = hitmarks.zero_damage.lit.id,
                    source = hitmarks.zero_damage.lit.id,
                    public = if (hit.hitmark.isPrivate) null else hitmarks.zero_damage.tint?.id,
                    damage = changedDamage,
                )
            val modifiedHit = hit.copy(hitmark = modifiedHitmark)
            takeHit(modifiedHit)
            return
        }

        if (changedDamage != null) {
            val modifiedHitmark = hit.hitmark.copy(damage = changedDamage)
            val modifiedHit = hit.copy(hitmark = modifiedHitmark)
            takeHit(modifiedHit)
            return
        }

        takeHit(hit)
    }

    private fun StandardNpcAccess.takeHit(hit: Hit) {
        check(hit.damage <= npc.hitpoints) {
            "Expected hit damage to be less than or equal to available hitpoints: " +
                "health=${npc.hitpoints}, hit=$hit"
        }

        // TODO(combat): Process recoils, retribution?, hero points, etc.
        // TODO(combat): onNpcHit script

        npc.hitpoints -= hit.damage

        val queueDeath = npc.hitpoints == 0 && queues.death !in npc.queueList
        if (queueDeath) {
            queueDeath()
        }

        npc.showHitmark(hit.hitmark)

        val visHeadbar = npc.visHeadbar(params.headbar)
        val headbar = hit.createHeadbar(npc.hitpoints, npc.baseHitpointsLvl, visHeadbar)
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
