package org.rsmod.plugins.api.movement

import org.rsmod.game.model.mob.move.MovementSpeed

public sealed class MoveSpeed(override val steps: Int) : MovementSpeed {

    public object Walk : MoveSpeed(steps = 1)
    public object Run : MoveSpeed(steps = 2)
}
