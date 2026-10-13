package org.runerealm.api.game.process.player

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.runerealm.api.config.refs.stats
import org.runerealm.api.config.refs.varbits
import org.runerealm.api.death.plugin.PlayerDeathScript
import org.runerealm.api.hit.plugin.PlayerHitScript
import org.runerealm.api.player.hit.queueHit
import org.runerealm.api.player.queueDeath
import org.runerealm.api.player.stat.hitpoints
import org.runerealm.api.player.stat.stat
import org.runerealm.api.player.vars.VarPlayerIntMapSetter
import org.runerealm.api.testing.GameTestState
import org.runerealm.game.hit.HitType
import org.runerealm.map.CoordGrid

/** Smite, Retribution and protection prayers in PvP, per the wiki. */
class CombatPrayerTest {
    @Test
    fun GameTestState.`smite drains a quarter of the damage from the target's prayer`() =
        runGameTest(PlayerHitScript::class) {
            val attacker = registerPlayer(coords = WILDERNESS.translateX(1))
            player.placeAt(WILDERNESS)
            player.stats[stats.hitpoints] = 99
            player.stats[stats.prayer] = 70
            VarPlayerIntMapSetter.set(attacker, varbits.smite, 1)

            player.queueHit(attacker, delay = 1, HitType.Melee, damage = 23)
            advance(ticks = 1)

            assertEquals(99 - 23, player.hitpoints)
            assertEquals(70 - 5, player.stat(stats.prayer)) // floor(23 / 4)
        }

    @Test
    fun GameTestState.`smite does not drain below four damage or without the prayer`() =
        runGameTest(PlayerHitScript::class) {
            val attacker = registerPlayer(coords = WILDERNESS.translateX(1))
            player.placeAt(WILDERNESS)
            player.stats[stats.hitpoints] = 99
            player.stats[stats.prayer] = 70

            player.queueHit(attacker, delay = 1, HitType.Melee, damage = 30)
            advance(ticks = 1)
            assertEquals(70, player.stat(stats.prayer))

            VarPlayerIntMapSetter.set(attacker, varbits.smite, 1)
            player.queueHit(attacker, delay = 1, HitType.Melee, damage = 3)
            advance(ticks = 1)
            assertEquals(70, player.stat(stats.prayer))
        }

    @Test
    fun GameTestState.`protection prayers block 40 percent of player damage`() =
        runGameTest(PlayerHitScript::class) {
            val attacker = registerPlayer(coords = WILDERNESS.translateX(1))
            player.placeAt(WILDERNESS)
            player.stats[stats.hitpoints] = 99
            player.stats[stats.prayer] = 70
            VarPlayerIntMapSetter.set(player, varbits.protect_from_melee, 1)
            VarPlayerIntMapSetter.set(attacker, varbits.smite, 1)

            player.queueHit(attacker, delay = 1, HitType.Melee, damage = 20)
            advance(ticks = 1)

            assertEquals(99 - 12, player.hitpoints)
            assertEquals(70 - 3, player.stat(stats.prayer)) // Smite uses the damage dealt.
        }

    @Test
    fun GameTestState.`retribution hits the killer on death`() =
        runGameTest(PlayerHitScript::class, PlayerDeathScript::class) {
            val killer = registerPlayer()
            killer.teleport(WILDERNESS.translateX(1))
            killer.stats[stats.hitpoints] = 99
            player.teleport(WILDERNESS)
            advance() // Registers both players in their zones.
            player.stats[stats.prayer] = 99
            VarPlayerIntMapSetter.set(player, varbits.retribution, 1)
            player.heroPoints(killer, 10)

            random.next = 17
            player.queueDeath()
            advance(ticks = 3)

            assertEquals(99 - 17, killer.hitpoints)
        }

    @Test
    fun GameTestState.`retribution only reaches adjacent players in the wilderness`() =
        runGameTest(PlayerHitScript::class, PlayerDeathScript::class) {
            val far = registerPlayer()
            far.teleport(WILDERNESS.translateX(2))
            far.stats[stats.hitpoints] = 99
            player.teleport(WILDERNESS)
            advance() // Registers both players in their zones.
            player.stats[stats.prayer] = 99
            VarPlayerIntMapSetter.set(player, varbits.retribution, 1)
            player.heroPoints(far, 10)

            player.queueDeath()
            advance(ticks = 3)
            assertEquals(99, far.hitpoints)
        }

    @Test
    fun GameTestState.`retribution does not hit players outside the wilderness`() =
        runGameTest(PlayerHitScript::class, PlayerDeathScript::class) {
            val outside = CoordGrid(3222, 3218, 0)
            val other = registerPlayer()
            other.teleport(outside.translateX(1))
            other.stats[stats.hitpoints] = 99
            player.teleport(outside)
            advance() // Registers both players in their zones.
            player.stats[stats.prayer] = 99
            VarPlayerIntMapSetter.set(player, varbits.retribution, 1)
            player.heroPoints(other, 10)

            random.next = 17
            player.queueDeath()
            advance(ticks = 3)
            assertEquals(99, other.hitpoints)
        }

    private companion object {
        val WILDERNESS = CoordGrid(3100, 3600, 0)
    }
}
