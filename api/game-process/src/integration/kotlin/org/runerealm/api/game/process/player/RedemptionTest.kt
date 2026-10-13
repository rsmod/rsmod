package org.runerealm.api.game.process.player

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.runerealm.api.config.refs.stats
import org.runerealm.api.config.refs.varbits
import org.runerealm.api.hit.plugin.PlayerHitScript
import org.runerealm.api.player.hit.queueHit
import org.runerealm.api.player.stat.hitpoints
import org.runerealm.api.player.stat.stat
import org.runerealm.api.player.vars.VarPlayerIntMapSetter
import org.runerealm.api.testing.GameTestState
import org.runerealm.api.testing.factory.npcTypeFactory
import org.runerealm.game.entity.npc.NpcMode
import org.runerealm.game.hit.HitType
import org.runerealm.map.CoordGrid

class RedemptionTest {
    @Test
    fun GameTestState.`redemption heals a quarter of prayer when hit below 10 percent`() =
        runGameTest(PlayerHitScript::class) {
            val npc = spawnNpc(CoordGrid(0, 50, 50, 0, 0), dummy)
            player.placeAt(npc.coords.translateZ(1))
            player.stats[stats.hitpoints] = 99
            player.stats[stats.prayer] = 80
            VarPlayerIntMapSetter.set(player, varbits.redemption, 1)

            player.queueHit(npc, delay = 1, HitType.Typeless, damage = 91)
            advance(ticks = 1)

            assertEquals(8 + 20, player.hitpoints)
            assertEquals(0, player.stat(stats.prayer))
        }

    @Test
    fun GameTestState.`redemption does not trigger above 10 percent`() =
        runGameTest(PlayerHitScript::class) {
            val npc = spawnNpc(CoordGrid(0, 50, 50, 0, 0), dummy)
            player.placeAt(npc.coords.translateZ(1))
            player.stats[stats.hitpoints] = 99
            player.stats[stats.prayer] = 80
            VarPlayerIntMapSetter.set(player, varbits.redemption, 1)

            player.queueHit(npc, delay = 1, HitType.Typeless, damage = 89)
            advance(ticks = 1)

            assertEquals(10, player.hitpoints)
            assertEquals(80, player.stat(stats.prayer))
        }

    private companion object {
        private val dummy =
            npcTypeFactory.create {
                name = "Dummy"
                size = 1
                hitpoints = 10
                defaultMode = NpcMode.None
            }
    }
}
