package org.rsmod.game.model.client

public class PlayerEntity : Entity(width = 1, height = 1) {

    public var name: String = ""

    public companion object {

        public val ZERO: PlayerEntity = PlayerEntity()
    }
}
