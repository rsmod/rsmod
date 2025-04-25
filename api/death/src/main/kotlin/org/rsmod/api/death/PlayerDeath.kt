package org.rsmod.api.death

import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.rsmod.api.config.constants
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
    private var Player.specialAttackEnergy by intVarp(varps.sa_energy)
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
            components.toplevel_target_pvpicons,
            components.toplevel_target_stats,
            components.toplevel_target_sidejournal,
            components.toplevel_target_wornitems,
            components.toplevel_target_prayerbook,
            components.toplevel_target_magicspellbook,
            components.toplevel_target_friends,
            components.toplevel_target_account,
            components.toplevel_target_sidechannels,
            components.toplevel_target_logout,
            components.toplevel_target_settingsside,
            components.toplevel_target_emote,
            components.toplevel_target_music,
        )
    }

    private fun ProtectedAccess.resetPlayerState(stats: StatTypeList) {
        player.disablePrayers()
        player.deathResetTimers()

        player.specialAttackType = 0
        player.specialAttackEnergy = constants.sa_max_energy
        player.skullIcon = null

        rebuildAppearance()

        camReset()
        statRestoreAll(stats.values)
        minimapReset()
    }
}
