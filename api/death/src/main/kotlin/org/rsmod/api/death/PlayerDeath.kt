package org.rsmod.api.death

import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.rsmod.api.config.refs.components
import org.rsmod.api.config.refs.jingles
import org.rsmod.api.config.refs.midis
import org.rsmod.api.config.refs.queues
import org.rsmod.api.config.refs.seqs
import org.rsmod.api.config.refs.varps
import org.rsmod.api.player.deathResetTimers
import org.rsmod.api.player.disablePrayers
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.vars.intVarp
import org.rsmod.game.entity.Player
import org.rsmod.game.type.stat.StatTypeList
import org.rsmod.map.CoordGrid

@Singleton
public class PlayerDeath @Inject constructor(private val statTypes: StatTypeList) {
    private var Player.specialAttackType by intVarp(varps.sa_attack)

    public suspend fun death(access: ProtectedAccess) {
        access.deathSequence()
    }

    private suspend fun ProtectedAccess.deathSequence() {
        val respawn = CoordGrid(0, 50, 50, 21, 18)
        val randomRespawn = mapFindSquareLineOfWalk(respawn, minRadius = 0, maxRadius = 2)
        stopAction()
        delay(2)
        anim(seqs.human_death)
        delay(4)
        combatClearQueue()
        clearQueue(queues.death)
        midiSong(midis.stop_music)
        midiJingle(jingles.death_jingle_2)
        mes("Oh dear, you are dead!")
        telejump(randomRespawn ?: respawn)
        resetAnim()
        // TODO: Drop death invs, etc.
        resetPlayerState(statTypes)
        restoreToplevelTabs(
            components.toplevel_target_pvp_icons,
            components.toplevel_target_side1,
            components.toplevel_target_side2,
            components.toplevel_target_side4,
            components.toplevel_target_side5,
            components.toplevel_target_side6,
            components.toplevel_target_side9,
            components.toplevel_target_side8,
            components.toplevel_target_side7,
            components.toplevel_target_side10,
            components.toplevel_target_side11,
            components.toplevel_target_side12,
            components.toplevel_target_side13,
        )
    }

    private fun ProtectedAccess.resetPlayerState(stats: StatTypeList) {
        player.disablePrayers()
        player.deathResetTimers()

        player.specialAttackType = 0
        player.skullIcon = null

        rebuildAppearance()

        camReset()
        statRestoreAll(stats.values)
        minimapReset()
    }
}
