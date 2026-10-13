package org.runerealm.api.game.process.player

import jakarta.inject.Inject
import org.junit.jupiter.api.Test
import org.runerealm.api.registry.player.PlayerRegistry
import org.runerealm.api.testing.GameTestState
import org.runerealm.game.entity.Player
import org.runerealm.map.zone.ZoneKey

class PlayerMapUpdateTest {
    @Test
    fun GameTestState.`register and change zones on login`() =
        runInjectedGameTest(PlayerRegistryDependency::class) {
            val startZone = ZoneKey(350, 350, 0)
            player.placeAt(startZone.toCoords())

            advance(ticks = 1)
            assertEquals(startZone, player.lastProcessedZone)
            assertEquals(it.registry.findAll(startZone).firstOrNull(), player)

            val nextZone = startZone.translateX(1)
            player.teleport(nextZone.toCoords())

            advance(ticks = 1)
            assertEquals(nextZone, player.lastProcessedZone)
            assertEquals(it.registry.findAll(nextZone).firstOrNull(), player)
            assertEquals(it.registry.findAll(startZone).toList(), emptyList<Player>())
        }

    private class PlayerRegistryDependency @Inject constructor(val registry: PlayerRegistry)
}
