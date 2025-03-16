package org.rsmod.api.player.hit.processor

import kotlin.math.min
import org.rsmod.api.config.refs.headbars
import org.rsmod.api.config.refs.queues
import org.rsmod.api.config.refs.stats
import org.rsmod.api.player.headbar.InternalPlayerHeadbars
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.stat.baseHitpointsLvl
import org.rsmod.api.player.stat.hitpoints
import org.rsmod.game.headbar.Headbar
import org.rsmod.game.hit.Hit
import org.rsmod.game.hit.HitType

public object StandardPlayerHitProcessor : QueuedPlayerHitProcessor {
    override fun ProtectedAccess.process(hit: Hit) {
        if (!hit.isValid(this)) {
            return
        }
        // TODO(combat): Process degradation, ring of recoil, retribution, hero points, etc.

        val damage = min(player.hitpoints, hit.damage)
        if (damage > 0) {
            statSub(stats.hitpoints, constant = damage, percent = 0)
        }

        val queueDeath = player.hitpoints == 0 && queues.death !in player.queueList
        if (queueDeath) {
            queueDeath()
        }

        player.showHitmark(hit.hitmark)

        val headbar = hit.createHeadbar(player.hitpoints, player.baseHitpointsLvl)
        player.showHeadbar(headbar)
    }

    private fun Hit.isValid(access: ProtectedAccess): Boolean {
        // Currently, we only have evidence of this validation being applied to hits dealt by npcs.
        if (!isFromNpc) {
            return true
        }
        // Only melee-based hits can be invalidated here.
        if (type != HitType.Melee) {
            return true
        }
        // If the npc that dealt the hit can no longer be found, the hit is invalidated. This can
        // occur when the npc's internal `uid` is reassigned (e.g., due to transmogrification).
        val npc = access.findHitNpcSource(this) ?: return false
        return npc.hitpoints > 0
    }

    private fun Hit.createHeadbar(currHp: Int, maxHp: Int): Headbar =
        InternalPlayerHeadbars.createFromHitmark(hitmark, currHp, maxHp, headbars.health_30)
}
