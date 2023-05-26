package org.rsmod.plugins.testing.simple

import org.rsmod.game.map.Coordinates
import org.rsmod.game.model.client.Entity
import org.rsmod.game.model.mob.Player
import org.rsmod.game.model.mob.list.PlayerList
import org.rsmod.plugins.api.map.GameObject
import org.rsmod.plugins.api.map.ObjectShape
import org.rsmod.plugins.cache.config.obj.ObjectTypeBuilder
import org.rsmod.plugins.testing.GameTestScope

public class SimpleGameTestScope {

    private val backingScope: GameTestScope = GameTestScope()

    public val playerList: PlayerList = backingScope.playerList

    public fun withPlayer(
        player: Player = Player(),
        action: Player.() -> Unit
    ): Unit = backingScope.withPlayer(player, action)

    public fun createEntity(): Entity = backingScope.createEntity()

    public fun createGameObject(
        coords: Coordinates,
        rot: Int = 0,
        shape: ObjectShape = ObjectShape.CenterpieceStraight,
        init: ObjectTypeBuilder.() -> Unit
    ): GameObject = backingScope.createGameObject(coords, rot, shape, init)
}
