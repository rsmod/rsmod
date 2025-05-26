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
        build("ranged") {
            type = HuntType.Player
            checkVis = HuntVis.LineOfSight
            checkNotTooStrong = HuntCheckNotTooStrong.OutsideWilderness
            checkNotCombat = varps.lastcombat
            checkNotCombatSelf = varns.lastcombat
            checkNotBusy = false
            findKeepHunting = false
            findNewMode = NpcMode.ApPlayer2
        }

        build("constant_melee") {
            type = HuntType.Player
            checkVis = HuntVis.LineOfSight
            checkNotTooStrong = HuntCheckNotTooStrong.OutsideWilderness
            checkNotCombat = varps.lastcombat
            checkNotCombatSelf = varns.lastcombat
            checkNotBusy = false
            findKeepHunting = true
            findNewMode = NpcMode.OpPlayer2
        }

        build("constant_ranged") {
            type = HuntType.Player
            checkVis = HuntVis.LineOfSight
            checkNotTooStrong = HuntCheckNotTooStrong.OutsideWilderness
            checkNotCombat = varps.lastcombat
            checkNotCombatSelf = varns.lastcombat
            checkNotBusy = false
            findKeepHunting = true
            findNewMode = NpcMode.ApPlayer2
        }

        build("cowardly") {
            type = HuntType.Player
            checkVis = HuntVis.LineOfSight
            checkNotTooStrong = HuntCheckNotTooStrong.OutsideWilderness
            checkNotCombat = varps.lastcombat
            checkNotCombatSelf = varns.lastcombat
            checkNotBusy = false
            findKeepHunting = false
            findNewMode = NpcMode.OpPlayer2
        }

        build("notbusy_melee") {
            type = HuntType.Player
            checkVis = HuntVis.LineOfSight
            checkNotTooStrong = HuntCheckNotTooStrong.OutsideWilderness
            checkNotCombat = varps.lastcombat
            checkNotCombatSelf = varns.lastcombat
            checkNotBusy = true
            findKeepHunting = false
            findNewMode = NpcMode.OpPlayer2
        }

        build("notbusy_range") {
            type = HuntType.Player
            checkVis = HuntVis.LineOfSight
            checkNotTooStrong = HuntCheckNotTooStrong.OutsideWilderness
            checkNotCombat = varps.lastcombat
            checkNotCombatSelf = varns.lastcombat
            checkNotBusy = true
            findKeepHunting = false
            findNewMode = NpcMode.ApPlayer2
        }

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

        build("aggressive_melee_extra") {
            type = HuntType.Player
            checkVis = HuntVis.Off
            checkNotTooStrong = HuntCheckNotTooStrong.Off
            checkNotCombat = varps.lastcombat
            checkNotCombatSelf = varns.lastcombat
            checkNotBusy = false
            findKeepHunting = false
            findNewMode = NpcMode.OpPlayer2
        }

        build("aggressive_ranged") {
            type = HuntType.Player
            checkVis = HuntVis.LineOfSight
            checkNotTooStrong = HuntCheckNotTooStrong.Off
            checkNotCombat = varps.lastcombat
            checkNotCombatSelf = varns.lastcombat
            checkNotBusy = false
            findKeepHunting = false
            findNewMode = NpcMode.ApPlayer2
        }
    }
}
