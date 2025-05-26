package org.rsmod.api.game.process.player

import jakarta.inject.Inject
import org.junit.jupiter.api.Test
import org.rsmod.api.registry.player.PlayerRegistry
import org.rsmod.api.testing.GameTestState
import org.rsmod.game.entity.Player
import org.rsmod.map.zone.ZoneKey

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
