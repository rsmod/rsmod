package org.runerealm.api.player.hit.processor

import kotlin.math.min
import org.runerealm.api.config.refs.headbars
import org.runerealm.api.config.refs.queues
import org.runerealm.api.config.refs.stats
import org.runerealm.api.player.headbar.InternalPlayerHeadbars
import org.runerealm.api.player.queueDeath
import org.runerealm.api.player.stat.baseHitpointsLvl
import org.runerealm.api.player.stat.hitpoints
import org.runerealm.api.player.stat.statSub
import org.runerealm.game.entity.Player
import org.runerealm.game.headbar.Headbar
import org.runerealm.game.hit.Hit

public class DamageOnlyPlayerHitProcessor : InstantPlayerHitProcessor {
    override fun Player.process(hit: Hit) {
        val damage = min(hitpoints, hit.damage)
        if (damage > 0) {
            statSub(stats.hitpoints, constant = damage, percent = 0)
        }

        val queueDeath = hitpoints == 0 && queues.death !in queueList
        if (queueDeath) {
            queueDeath()
        }

        showHitmark(hit.hitmark)

        val headbar = hit.createHeadbar(hitpoints, baseHitpointsLvl)
        showHeadbar(headbar)
    }

    private fun Hit.createHeadbar(currHp: Int, maxHp: Int): Headbar =
        InternalPlayerHeadbars.createFromHitmark(hitmark, currHp, maxHp, headbars.health_30)
}
