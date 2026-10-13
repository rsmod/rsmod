package org.runerealm.api.config.builders

import org.runerealm.api.config.refs.varns
import org.runerealm.api.config.refs.varps
import org.runerealm.api.type.builders.hunt.HuntModeBuilder
import org.runerealm.game.entity.npc.NpcMode
import org.runerealm.game.type.hunt.HuntCheckNotTooStrong
import org.runerealm.game.type.hunt.HuntType
import org.runerealm.game.type.hunt.HuntVis

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
