package org.runerealm.api.repo.player

import jakarta.inject.Inject
import org.runerealm.api.registry.player.PlayerRegistry
import org.runerealm.game.entity.Player
import org.runerealm.map.CoordGrid
import org.runerealm.map.zone.ZoneKey

public class PlayerRepository @Inject constructor(private val registry: PlayerRegistry) {
    public fun hide(player: Player) {
        registry.hide(player)
    }

    public fun reveal(player: Player) {
        registry.reveal(player)
    }

    public fun findAll(zone: ZoneKey): Sequence<Player> = registry.findAll(zone)

    public fun findAll(coords: CoordGrid): Sequence<Player> =
        findAll(ZoneKey.from(coords)).filter { it.coords == coords }

    public fun findAll(zone: ZoneKey, zoneRadius: Int): Sequence<Player> {
        return sequence {
            for (x in -zoneRadius..zoneRadius) {
                for (z in -zoneRadius..zoneRadius) {
                    val translate = zone.translate(x, z)
                    val players = findAll(translate)
                    yieldAll(players)
                }
            }
        }
    }
}
