package org.rsmod.content.skills.woodcutting

import org.rsmod.api.config.refs.stats
import org.rsmod.api.stats.levelmod.InvisibleLevelMod
import org.rsmod.game.entity.Player

class WoodcuttingLevelBoosts : InvisibleLevelMod(stats.woodcutting) {
    override fun Player.calculateBoost(): Int {
        // TODO: Woodcutting guild area check for +7 boost.
        // TODO: Each player chopping same tree provides +1 boost (capped at +10).
        return 0
    }
}
