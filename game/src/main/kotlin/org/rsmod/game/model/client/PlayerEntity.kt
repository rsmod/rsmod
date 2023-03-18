package org.rsmod.game.model.client

public class PlayerEntity : MobEntity(size = 1) {

    public var name: String = ""

    public companion object {

        public val ZERO: PlayerEntity = PlayerEntity()
    }
}
