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

public object StandardPlayerHitProcessor : QueuedPlayerHitProcessor {
    override fun ProtectedAccess.process(hit: Hit) {
        // TODO(combat): Process degradation, ring of recoil, retribution, hero points, etc.

        val damage = min(player.hitpoints, hit.damage)
        if (damage > 0) {
            statSub(stats.hitpoints, constant = damage, percent = 0)
        }

        val queueDeath = player.hitpoints == 0 && queues.death !in player.queueList
        if (queueDeath) {
            // TODO(combat): queue death
        }

        player.showHitmark(hit.hitmark)

        val headbar = hit.createHeadbar(player.hitpoints, player.baseHitpointsLvl)
        player.showHeadbar(headbar)
    }

    private fun Hit.createHeadbar(currHp: Int, maxHp: Int): Headbar =
        InternalPlayerHeadbars.createFromHitmark(hitmark, currHp, maxHp, headbars.health_30)
}
