package org.runerealm.content.skills.woodcutting

import org.runerealm.api.config.refs.stats
import org.runerealm.api.stats.levelmod.InvisibleLevelMod
import org.runerealm.game.entity.Player

class WoodcuttingLevelBoosts : InvisibleLevelMod(stats.woodcutting) {
    override fun Player.calculateBoost(): Int {
        // TODO: Woodcutting guild area check for +7 boost.
        // TODO: Each player chopping same tree provides +1 boost (capped at +10).
        return 0
    }
}
