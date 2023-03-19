package org.rsmod.plugins.api.movement

import org.rsmod.game.model.mob.move.MovementSpeed

public enum class MoveSpeed(public val id: Int, override val steps: Int) : MovementSpeed {
    Walk(id = 0, steps = 1),
    Run(id = 1, steps = 2),
    Displace(id = 2, steps = 0);

    public companion object {

        public val values: Array<MoveSpeed> = enumValues()
    }
}
