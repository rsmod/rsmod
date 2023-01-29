package org.rsmod.game.model.client

public class PlayerEntity : Entity() {

    public var name: String = ""

    public companion object {

        public val ZERO: PlayerEntity = PlayerEntity()
    }
}
