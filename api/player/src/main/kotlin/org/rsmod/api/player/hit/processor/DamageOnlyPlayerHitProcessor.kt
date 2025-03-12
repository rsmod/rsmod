package org.rsmod.api.player.hit.processor

import jakarta.inject.Inject
import kotlin.math.min
import org.rsmod.api.config.refs.headbars
import org.rsmod.api.config.refs.queues
import org.rsmod.api.config.refs.stats
import org.rsmod.api.player.headbar.InternalPlayerHeadbars
import org.rsmod.api.player.stat.baseHitpointsLvl
import org.rsmod.api.player.stat.hitpoints
import org.rsmod.api.player.stat.statSub
import org.rsmod.api.stats.levelmod.InvisibleLevels
import org.rsmod.game.entity.Player
import org.rsmod.game.headbar.Headbar
import org.rsmod.game.hit.Hit

public class DamageOnlyPlayerHitProcessor
@Inject
constructor(private val invisibleLevels: InvisibleLevels) : InstantPlayerHitProcessor {
    override fun Player.process(hit: Hit) {
        val damage = min(hitpoints, hit.damage)
        if (damage > 0) {
            statSub(stats.hitpoints, constant = damage, percent = 0, invisibleLevels)
        }

        val queueDeath = hitpoints == 0 && queues.death !in queueList
        if (queueDeath) {
            // TODO(combat): queue death
        }

        showHitmark(hit.hitmark)

        val headbar = hit.createHeadbar(hitpoints, baseHitpointsLvl)
        showHeadbar(headbar)
    }

    private fun Hit.createHeadbar(currHp: Int, maxHp: Int): Headbar =
        InternalPlayerHeadbars.createFromHitmark(hitmark, currHp, maxHp, headbars.health_30)
}
