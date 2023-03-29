package org.rsmod.plugins.api.move

import org.rsmod.game.model.mob.move.MovementSpeed

public enum class MoveSpeed(override val steps: Int) : MovementSpeed {

    Crawl(steps = 1),
    Walk(steps = 1),
    Run(steps = 2),
    Displace(steps = 0);

    public companion object {

        public val values: Array<MoveSpeed> = enumValues()
    }
}
