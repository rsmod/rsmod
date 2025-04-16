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
            components.wilderness_overlay_target,
            components.skills_tab_target,
            components.journal_header_tab_target,
            components.equipment_tab_target,
            components.prayer_tab_target,
            components.spellbook_tab_target,
            components.friend_list_tab_target,
            components.account_management_tab_target,
            components.chat_header_target,
            // components.logout_tab_target, // TODO
            components.settings_tab_target,
            components.emote_tab_target,
            components.music_tab_target,
        )
    }

    private fun ProtectedAccess.resetPlayerState(stats: StatTypeList) {
        player.specialAttackType = 0
        player.specialAttackEnergy = constants.sa_default_energy
        player.skullIcon = null

        rebuildAppearance()

        camReset()
        statRestoreAll(stats.values)
        minimapReset()
    }
}
