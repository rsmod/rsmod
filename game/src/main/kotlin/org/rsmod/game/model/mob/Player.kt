package org.rsmod.game.model.mob

import org.rsmod.game.model.DownstreamList
import org.rsmod.game.model.client.PlayerEntity

public class Player(override val entity: PlayerEntity = PlayerEntity()) : Mob() {

    public val downstream: DownstreamList = DownstreamList()

    public var username: String = ""

    public var displayName: String
        get() = entity.name
        set(value) { entity.name = value }
}
