package org.rsmod.api.config.builders

import org.rsmod.api.config.refs.varns
import org.rsmod.api.config.refs.varps
import org.rsmod.api.type.builders.hunt.HuntModeBuilder
import org.rsmod.game.entity.npc.NpcMode
import org.rsmod.game.type.hunt.HuntCheckNotTooStrong
import org.rsmod.game.type.hunt.HuntType
import org.rsmod.game.type.hunt.HuntVis

internal object HuntBuilds : HuntModeBuilder() {
    init {
        build("aggressive_melee") {
            type = HuntType.Player
            checkVis = HuntVis.LineOfSight
            checkNotTooStrong = HuntCheckNotTooStrong.Off
            checkNotCombat = varps.lastcombat
            checkNotCombatSelf = varns.lastcombat
            checkNotBusy = false
            findKeepHunting = false
            findNewMode = NpcMode.OpPlayer2
        }
    }
}
