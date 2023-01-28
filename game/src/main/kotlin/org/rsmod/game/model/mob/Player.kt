package org.rsmod.game.model.mob

import org.rsmod.game.model.DownstreamList
import org.rsmod.game.model.client.PlayerEntity

public class Player(override val entity: PlayerEntity) : Mob() {

    public val downstream: DownstreamList = DownstreamList()
}
